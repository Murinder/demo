package com.example.partnerservice.service;

import com.example.partnerservice.dto.CreateVacancyDto;
import com.example.partnerservice.dto.PartnerEvent;
import com.example.partnerservice.dto.VacancyDto;
import com.example.partnerservice.model.Partner;
import com.example.partnerservice.model.PartnerContact;
import com.example.partnerservice.model.Vacancy;
import com.example.partnerservice.model.enums.VacancyType;
import com.example.partnerservice.repository.PartnerContactRepository;
import com.example.partnerservice.repository.PartnerRepository;
import com.example.partnerservice.repository.VacancyRepository;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final PartnerRepository partnerRepository;
    private final PartnerContactRepository contactRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public VacancyDto create(CreateVacancyDto dto) {
        Partner partner = partnerRepository.findById(dto.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", dto.getPartnerId()));

        PartnerContact contact = null;
        if (dto.getContactId() != null) {
            contact = contactRepository.findById(dto.getContactId())
                    .orElseThrow(() -> new ResourceNotFoundException("PartnerContact", "id", dto.getContactId()));
        }

        Vacancy vacancy = Vacancy.builder()
                .partner(partner)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .requirements(dto.getRequirements())
                .vacancyType(VacancyType.valueOf(dto.getVacancyType()))
                .location(dto.getLocation())
                .salaryRange(dto.getSalaryRange())
                .applicationDeadline(dto.getApplicationDeadline())
                .contact(contact)
                .build();
        Vacancy saved = vacancyRepository.save(vacancy);

        PartnerEvent event = PartnerEvent.builder()
                .entityId(saved.getId())
                .partnerId(partner.getId())
                .title(saved.getTitle())
                .entityType("VACANCY")
                .build();
        event.init("partner-service");
        eventPublisher.publish(
                RabbitMqAutoConfiguration.PARTNER_EXCHANGE,
                RabbitMqAutoConfiguration.PARTNER_VACANCY_PUBLISHED_KEY,
                event
        );

        return toDto(saved);
    }

    public VacancyDto getById(UUID id) {
        Vacancy vacancy = vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy", "id", id));
        return toDto(vacancy);
    }

    public List<VacancyDto> getAll() {
        return vacancyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<VacancyDto> getActiveVacancies() {
        return vacancyRepository.findByIsActiveTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<VacancyDto> getByPartnerId(UUID partnerId) {
        return vacancyRepository.findByPartnerId(partnerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<VacancyDto> getByVacancyType(VacancyType type) {
        return vacancyRepository.findByVacancyType(type).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private VacancyDto toDto(Vacancy v) {
        return VacancyDto.builder()
                .id(v.getId())
                .partnerId(v.getPartner().getId())
                .title(v.getTitle())
                .description(v.getDescription())
                .requirements(v.getRequirements())
                .isActive(v.getIsActive())
                .vacancyType(v.getVacancyType() != null ? v.getVacancyType().name() : null)
                .location(v.getLocation())
                .salaryRange(v.getSalaryRange())
                .applicationDeadline(v.getApplicationDeadline())
                .contactId(v.getContact() != null ? v.getContact().getId() : null)
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }
}
