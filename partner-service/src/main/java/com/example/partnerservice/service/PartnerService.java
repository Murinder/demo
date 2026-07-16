package com.example.partnerservice.service;

import com.example.partnerservice.dto.CreatePartnerDto;
import com.example.partnerservice.dto.PartnerDto;
import com.example.partnerservice.model.Partner;
import com.example.partnerservice.model.enums.PartnershipStatus;
import com.example.partnerservice.repository.PartnerRepository;
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
public class PartnerService {

    private final PartnerRepository partnerRepository;

    @Transactional
    public PartnerDto create(CreatePartnerDto dto) {
        Partner partner = Partner.builder()
                .companyName(dto.getCompanyName())
                .contactInfo(dto.getContactInfo())
                .website(dto.getWebsite())
                .industry(dto.getIndustry())
                .logoUrl(dto.getLogoUrl())
                .description(dto.getDescription())
                .build();
        return toDto(partnerRepository.save(partner));
    }

    public PartnerDto getById(UUID id) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", id));
        return toDto(partner);
    }

    public List<PartnerDto> getAll() {
        return partnerRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<PartnerDto> getActivePartners() {
        return partnerRepository.findByIsActiveTrue().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PartnerDto update(UUID id, CreatePartnerDto dto) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", id));
        partner.setCompanyName(dto.getCompanyName());
        partner.setContactInfo(dto.getContactInfo());
        partner.setWebsite(dto.getWebsite());
        partner.setIndustry(dto.getIndustry());
        partner.setLogoUrl(dto.getLogoUrl());
        partner.setDescription(dto.getDescription());
        return toDto(partnerRepository.save(partner));
    }

    @Transactional
    public PartnerDto updateStatus(UUID id, PartnershipStatus status) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", id));
        partner.setPartnershipStatus(status);
        return toDto(partnerRepository.save(partner));
    }

    private PartnerDto toDto(Partner p) {
        return PartnerDto.builder()
                .id(p.getId())
                .companyName(p.getCompanyName())
                .contactInfo(p.getContactInfo())
                .isActive(p.getIsActive())
                .website(p.getWebsite())
                .industry(p.getIndustry())
                .partnershipStatus(p.getPartnershipStatus() != null ? p.getPartnershipStatus().name() : null)
                .logoUrl(p.getLogoUrl())
                .description(p.getDescription())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
