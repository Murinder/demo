package com.example.partnerservice.service;

import com.example.partnerservice.dto.CaseDto;
import com.example.partnerservice.dto.CreateCaseDto;
import com.example.partnerservice.dto.PartnerEvent;
import com.example.partnerservice.model.Case;
import com.example.partnerservice.model.Partner;
import com.example.partnerservice.repository.CaseRepository;
import com.example.partnerservice.repository.PartnerRepository;
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
public class CaseService {

    private final CaseRepository caseRepository;
    private final PartnerRepository partnerRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public CaseDto create(CreateCaseDto dto) {
        Partner partner = partnerRepository.findById(dto.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", dto.getPartnerId()));
        Case caseEntity = Case.builder()
                .partner(partner)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .difficulty(dto.getDifficulty())
                .requiredSkills(dto.getRequiredSkills())
                .rewardDescription(dto.getRewardDescription())
                .expectedDuration(dto.getExpectedDuration())
                .build();
        Case saved = caseRepository.save(caseEntity);

        PartnerEvent event = PartnerEvent.builder()
                .entityId(saved.getId())
                .partnerId(partner.getId())
                .title(saved.getTitle())
                .entityType("CASE")
                .build();
        event.init("partner-service");
        eventPublisher.publish(
                RabbitMqAutoConfiguration.PARTNER_EXCHANGE,
                RabbitMqAutoConfiguration.PARTNER_CASE_PUBLISHED_KEY,
                event
        );

        return toDto(saved);
    }

    public CaseDto getById(UUID id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", id));
        return toDto(caseEntity);
    }

    public List<CaseDto> getAll() {
        return caseRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CaseDto> getActiveCases() {
        return caseRepository.findByIsActiveTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CaseDto> getByPartnerId(UUID partnerId) {
        return caseRepository.findByPartnerId(partnerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CaseDto> getByDifficulty(String difficulty) {
        return caseRepository.findByDifficulty(difficulty).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private CaseDto toDto(Case c) {
        return CaseDto.builder()
                .id(c.getId())
                .partnerId(c.getPartner().getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .difficulty(c.getDifficulty())
                .requiredSkills(c.getRequiredSkills())
                .rewardDescription(c.getRewardDescription())
                .isActive(c.getIsActive())
                .expectedDuration(c.getExpectedDuration())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
