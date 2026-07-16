package com.example.partnerservice.service;

import com.example.partnerservice.dto.AgreementDto;
import com.example.partnerservice.dto.CreateAgreementDto;
import com.example.partnerservice.model.Agreement;
import com.example.partnerservice.model.Partner;
import com.example.partnerservice.model.enums.AgreementStatus;
import com.example.partnerservice.repository.AgreementRepository;
import com.example.partnerservice.repository.PartnerRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgreementService {

    private final AgreementRepository agreementRepository;
    private final PartnerRepository partnerRepository;

    @Transactional
    public AgreementDto create(CreateAgreementDto dto) {
        Partner partner = partnerRepository.findById(dto.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", dto.getPartnerId()));
        Agreement agreement = Agreement.builder()
                .partner(partner)
                .projectId(dto.getProjectId())
                .documentPath(dto.getDocumentPath())
                .expiresAt(dto.getExpiresAt())
                .description(dto.getDescription())
                .build();
        return toDto(agreementRepository.save(agreement));
    }

    public AgreementDto getById(UUID id) {
        Agreement agreement = agreementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agreement", "id", id));
        return toDto(agreement);
    }

    public List<AgreementDto> getByPartnerId(UUID partnerId) {
        return agreementRepository.findByPartnerId(partnerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AgreementDto updateStatus(UUID id, AgreementStatus status) {
        Agreement agreement = agreementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agreement", "id", id));
        agreement.setStatus(status);
        if (status == AgreementStatus.SIGNED) {
            agreement.setSignedAt(OffsetDateTime.now());
        }
        return toDto(agreementRepository.save(agreement));
    }

    private AgreementDto toDto(Agreement a) {
        return AgreementDto.builder()
                .id(a.getId())
                .partnerId(a.getPartner().getId())
                .projectId(a.getProjectId())
                .documentPath(a.getDocumentPath())
                .status(a.getStatus() != null ? a.getStatus().name() : null)
                .signedAt(a.getSignedAt())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .expiresAt(a.getExpiresAt())
                .version(a.getVersion())
                .description(a.getDescription())
                .build();
    }
}
