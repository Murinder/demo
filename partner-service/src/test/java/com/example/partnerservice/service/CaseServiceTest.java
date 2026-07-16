package com.example.partnerservice.service;

import com.example.partnerservice.dto.CaseDto;
import com.example.partnerservice.dto.CreateCaseDto;
import com.example.partnerservice.model.Case;
import com.example.partnerservice.model.Partner;
import com.example.partnerservice.repository.CaseRepository;
import com.example.partnerservice.repository.PartnerRepository;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private CaseService caseService;

    private Partner createPartner() {
        return Partner.builder()
                .id(UUID.randomUUID())
                .companyName("Test Partner")
                .isActive(true)
                .build();
    }

    @Test
    void create_ValidDto_SavesAndPublishesEvent() {
        Partner partner = createPartner();
        UUID partnerId = partner.getId();

        CreateCaseDto dto = CreateCaseDto.builder()
                .partnerId(partnerId)
                .title("Test Case")
                .description("Description")
                .difficulty("MEDIUM")
                .requiredSkills(List.of("Java", "Spring"))
                .rewardDescription("Certificate")
                .expectedDuration("2 weeks")
                .build();

        when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(partner));
        when(caseRepository.save(any(Case.class))).thenAnswer(inv -> {
            Case c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        CaseDto result = caseService.create(dto);

        assertNotNull(result);
        assertEquals("Test Case", result.getTitle());
        assertEquals("MEDIUM", result.getDifficulty());
        assertEquals(partnerId, result.getPartnerId());
        verify(eventPublisher).publish(eq("etsopy.partner"), eq("partner.case_published"), any());
    }

    @Test
    void create_NonExistingPartner_ThrowsException() {
        UUID partnerId = UUID.randomUUID();
        CreateCaseDto dto = CreateCaseDto.builder()
                .partnerId(partnerId)
                .title("Case")
                .description("Desc")
                .build();

        when(partnerRepository.findById(partnerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> caseService.create(dto));
        verify(caseRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any(), any());
    }

    @Test
    void getById_ExistingCase_ReturnsDto() {
        Partner partner = createPartner();
        UUID caseId = UUID.randomUUID();
        Case caseEntity = Case.builder()
                .id(caseId)
                .partner(partner)
                .title("Found Case")
                .description("Desc")
                .difficulty("EASY")
                .isActive(true)
                .build();

        when(caseRepository.findById(caseId)).thenReturn(Optional.of(caseEntity));

        CaseDto result = caseService.getById(caseId);

        assertEquals(caseId, result.getId());
        assertEquals("Found Case", result.getTitle());
        assertEquals(partner.getId(), result.getPartnerId());
    }

    @Test
    void getById_NonExistingCase_ThrowsException() {
        UUID caseId = UUID.randomUUID();
        when(caseRepository.findById(caseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> caseService.getById(caseId));
    }

    @Test
    void getAll_ReturnsList() {
        Partner partner = createPartner();
        Case c1 = Case.builder().id(UUID.randomUUID()).partner(partner).title("A").description("D").build();
        Case c2 = Case.builder().id(UUID.randomUUID()).partner(partner).title("B").description("D").build();

        when(caseRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CaseDto> result = caseService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void getActiveCases_ReturnsOnlyActive() {
        Partner partner = createPartner();
        Case active = Case.builder().id(UUID.randomUUID()).partner(partner).title("Active").description("D").isActive(true).build();

        when(caseRepository.findByIsActiveTrue()).thenReturn(List.of(active));

        List<CaseDto> result = caseService.getActiveCases();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
    }

    @Test
    void getByPartnerId_ReturnsCasesForPartner() {
        Partner partner = createPartner();
        UUID partnerId = partner.getId();
        Case c = Case.builder().id(UUID.randomUUID()).partner(partner).title("Partner Case").description("D").build();

        when(caseRepository.findByPartnerId(partnerId)).thenReturn(List.of(c));

        List<CaseDto> result = caseService.getByPartnerId(partnerId);

        assertEquals(1, result.size());
        assertEquals(partnerId, result.get(0).getPartnerId());
    }

    @Test
    void getByDifficulty_FiltersCorrectly() {
        Partner partner = createPartner();
        Case c = Case.builder().id(UUID.randomUUID()).partner(partner).title("Hard").description("D").difficulty("HARD").build();

        when(caseRepository.findByDifficulty("HARD")).thenReturn(List.of(c));

        List<CaseDto> result = caseService.getByDifficulty("HARD");

        assertEquals(1, result.size());
        assertEquals("HARD", result.get(0).getDifficulty());
    }
}
