package com.example.partnerservice.service;

import com.example.partnerservice.dto.CreatePartnerDto;
import com.example.partnerservice.dto.PartnerDto;
import com.example.partnerservice.model.Partner;
import com.example.partnerservice.model.enums.PartnershipStatus;
import com.example.partnerservice.repository.PartnerRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartnerServiceTest {

    @Mock
    private PartnerRepository partnerRepository;

    @InjectMocks
    private PartnerService partnerService;

    @Test
    void create_ShouldReturnPartnerDto() {
        CreatePartnerDto dto = CreatePartnerDto.builder()
                .companyName("Test Company")
                .contactInfo("info@test.com")
                .website("https://test.com")
                .industry("IT")
                .description("A test partner")
                .build();

        Partner saved = Partner.builder()
                .id(UUID.randomUUID())
                .companyName("Test Company")
                .contactInfo("info@test.com")
                .website("https://test.com")
                .industry("IT")
                .description("A test partner")
                .partnershipStatus(PartnershipStatus.PROSPECTIVE)
                .isActive(true)
                .build();

        when(partnerRepository.save(any(Partner.class))).thenReturn(saved);

        PartnerDto result = partnerService.create(dto);

        assertNotNull(result);
        assertEquals("Test Company", result.getCompanyName());
        assertEquals("PROSPECTIVE", result.getPartnershipStatus());
        assertTrue(result.getIsActive());
        verify(partnerRepository).save(any(Partner.class));
    }

    @Test
    void getById_ExistingPartner_ReturnsDto() {
        UUID id = UUID.randomUUID();
        Partner partner = Partner.builder()
                .id(id)
                .companyName("Existing Company")
                .partnershipStatus(PartnershipStatus.ACTIVE)
                .isActive(true)
                .build();

        when(partnerRepository.findById(id)).thenReturn(Optional.of(partner));

        PartnerDto result = partnerService.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Existing Company", result.getCompanyName());
    }

    @Test
    void getById_NonExistingPartner_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(partnerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partnerService.getById(id));
    }

    @Test
    void getAll_ReturnsList() {
        Partner p1 = Partner.builder().id(UUID.randomUUID()).companyName("A").partnershipStatus(PartnershipStatus.ACTIVE).isActive(true).build();
        Partner p2 = Partner.builder().id(UUID.randomUUID()).companyName("B").partnershipStatus(PartnershipStatus.PROSPECTIVE).isActive(true).build();

        when(partnerRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PartnerDto> result = partnerService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void update_ExistingPartner_UpdatesFields() {
        UUID id = UUID.randomUUID();
        Partner existing = Partner.builder()
                .id(id)
                .companyName("Old Name")
                .partnershipStatus(PartnershipStatus.PROSPECTIVE)
                .isActive(true)
                .build();

        CreatePartnerDto dto = CreatePartnerDto.builder()
                .companyName("New Name")
                .website("https://new.com")
                .build();

        when(partnerRepository.findById(id)).thenReturn(Optional.of(existing));
        when(partnerRepository.save(any(Partner.class))).thenAnswer(inv -> inv.getArgument(0));

        PartnerDto result = partnerService.update(id, dto);

        assertEquals("New Name", result.getCompanyName());
        assertEquals("https://new.com", result.getWebsite());
    }

    @Test
    void updateStatus_SetsNewStatus() {
        UUID id = UUID.randomUUID();
        Partner partner = Partner.builder()
                .id(id)
                .companyName("Company")
                .partnershipStatus(PartnershipStatus.PROSPECTIVE)
                .isActive(true)
                .build();

        when(partnerRepository.findById(id)).thenReturn(Optional.of(partner));
        when(partnerRepository.save(any(Partner.class))).thenAnswer(inv -> inv.getArgument(0));

        PartnerDto result = partnerService.updateStatus(id, PartnershipStatus.ACTIVE);

        assertEquals("ACTIVE", result.getPartnershipStatus());
    }

    @Test
    void getActivePartners_ReturnsOnlyActive() {
        Partner active = Partner.builder()
                .id(UUID.randomUUID())
                .companyName("Active Corp")
                .partnershipStatus(PartnershipStatus.ACTIVE)
                .isActive(true)
                .build();

        when(partnerRepository.findByIsActiveTrue()).thenReturn(List.of(active));

        List<PartnerDto> result = partnerService.getActivePartners();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
        assertEquals("Active Corp", result.get(0).getCompanyName());
    }

    @Test
    void update_NonExistingPartner_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(partnerRepository.findById(id)).thenReturn(Optional.empty());

        CreatePartnerDto dto = CreatePartnerDto.builder().companyName("X").build();

        assertThrows(ResourceNotFoundException.class, () -> partnerService.update(id, dto));
        verify(partnerRepository, never()).save(any());
    }

    @Test
    void updateStatus_NonExistingPartner_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(partnerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> partnerService.updateStatus(id, PartnershipStatus.ACTIVE));
    }
}
