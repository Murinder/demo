package com.example.partnerservice.service;

import com.example.partnerservice.dto.CreateVacancyDto;
import com.example.partnerservice.dto.VacancyDto;
import com.example.partnerservice.model.Partner;
import com.example.partnerservice.model.PartnerContact;
import com.example.partnerservice.model.Vacancy;
import com.example.partnerservice.model.enums.VacancyType;
import com.example.partnerservice.repository.PartnerContactRepository;
import com.example.partnerservice.repository.PartnerRepository;
import com.example.partnerservice.repository.VacancyRepository;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @Mock
    private VacancyRepository vacancyRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private PartnerContactRepository contactRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private VacancyService vacancyService;

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

        CreateVacancyDto dto = CreateVacancyDto.builder()
                .partnerId(partnerId)
                .title("Java Developer")
                .description("Backend position")
                .requirements("3+ years experience")
                .vacancyType("INTERNSHIP")
                .location("Kyiv")
                .salaryRange("$1000-$2000")
                .applicationDeadline(LocalDate.of(2026, 6, 1))
                .build();

        when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(partner));
        when(vacancyRepository.save(any(Vacancy.class))).thenAnswer(inv -> {
            Vacancy v = inv.getArgument(0);
            v.setId(UUID.randomUUID());
            return v;
        });

        VacancyDto result = vacancyService.create(dto);

        assertNotNull(result);
        assertEquals("Java Developer", result.getTitle());
        assertEquals("INTERNSHIP", result.getVacancyType());
        assertEquals(partnerId, result.getPartnerId());
        assertEquals("Kyiv", result.getLocation());
        verify(eventPublisher).publish(eq("etsopy.partner"), eq("partner.vacancy_published"), any());
    }

    @Test
    void create_WithContact_SavesWithContact() {
        Partner partner = createPartner();
        UUID partnerId = partner.getId();
        UUID contactId = UUID.randomUUID();
        PartnerContact contact = PartnerContact.builder()
                .id(contactId)
                .partner(partner)
                .name("John")
                .email("john@test.com")
                .build();

        CreateVacancyDto dto = CreateVacancyDto.builder()
                .partnerId(partnerId)
                .title("Designer")
                .description("UI position")
                .vacancyType("FULL_TIME")
                .contactId(contactId)
                .build();

        when(partnerRepository.findById(partnerId)).thenReturn(Optional.of(partner));
        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));
        when(vacancyRepository.save(any(Vacancy.class))).thenAnswer(inv -> {
            Vacancy v = inv.getArgument(0);
            v.setId(UUID.randomUUID());
            return v;
        });

        VacancyDto result = vacancyService.create(dto);

        assertNotNull(result);
        assertEquals(contactId, result.getContactId());
    }

    @Test
    void create_NonExistingPartner_ThrowsException() {
        UUID partnerId = UUID.randomUUID();
        CreateVacancyDto dto = CreateVacancyDto.builder()
                .partnerId(partnerId)
                .title("Vacancy")
                .description("Desc")
                .vacancyType("INTERNSHIP")
                .build();

        when(partnerRepository.findById(partnerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vacancyService.create(dto));
        verify(vacancyRepository, never()).save(any());
    }

    @Test
    void getById_ExistingVacancy_ReturnsDto() {
        Partner partner = createPartner();
        UUID vacancyId = UUID.randomUUID();
        Vacancy vacancy = Vacancy.builder()
                .id(vacancyId)
                .partner(partner)
                .title("QA Engineer")
                .description("Testing")
                .vacancyType(VacancyType.FULL_TIME)
                .isActive(true)
                .build();

        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        VacancyDto result = vacancyService.getById(vacancyId);

        assertEquals(vacancyId, result.getId());
        assertEquals("QA Engineer", result.getTitle());
        assertEquals("FULL_TIME", result.getVacancyType());
    }

    @Test
    void getById_NonExistingVacancy_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(vacancyRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vacancyService.getById(id));
    }

    @Test
    void getAll_ReturnsList() {
        Partner partner = createPartner();
        Vacancy v1 = Vacancy.builder().id(UUID.randomUUID()).partner(partner).title("A").description("D").vacancyType(VacancyType.INTERNSHIP).build();
        Vacancy v2 = Vacancy.builder().id(UUID.randomUUID()).partner(partner).title("B").description("D").vacancyType(VacancyType.FULL_TIME).build();

        when(vacancyRepository.findAll()).thenReturn(List.of(v1, v2));

        List<VacancyDto> result = vacancyService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void getActiveVacancies_ReturnsOnlyActive() {
        Partner partner = createPartner();
        Vacancy active = Vacancy.builder().id(UUID.randomUUID()).partner(partner).title("Active").description("D").vacancyType(VacancyType.PROJECT).isActive(true).build();

        when(vacancyRepository.findByIsActiveTrue()).thenReturn(List.of(active));

        List<VacancyDto> result = vacancyService.getActiveVacancies();

        assertEquals(1, result.size());
        assertTrue(result.get(0).getIsActive());
    }

    @Test
    void getByVacancyType_FiltersCorrectly() {
        Partner partner = createPartner();
        Vacancy v = Vacancy.builder().id(UUID.randomUUID()).partner(partner).title("Intern").description("D").vacancyType(VacancyType.INTERNSHIP).build();

        when(vacancyRepository.findByVacancyType(VacancyType.INTERNSHIP)).thenReturn(List.of(v));

        List<VacancyDto> result = vacancyService.getByVacancyType(VacancyType.INTERNSHIP);

        assertEquals(1, result.size());
        assertEquals("INTERNSHIP", result.get(0).getVacancyType());
    }
}
