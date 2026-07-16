package com.example.partnerservice.service;

import com.example.partnerservice.dto.CreatePartnerContactDto;
import com.example.partnerservice.dto.PartnerContactDto;
import com.example.partnerservice.model.Partner;
import com.example.partnerservice.model.PartnerContact;
import com.example.partnerservice.repository.PartnerContactRepository;
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
public class PartnerContactService {

    private final PartnerContactRepository contactRepository;
    private final PartnerRepository partnerRepository;

    @Transactional
    public PartnerContactDto create(CreatePartnerContactDto dto) {
        Partner partner = partnerRepository.findById(dto.getPartnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", dto.getPartnerId()));
        PartnerContact contact = PartnerContact.builder()
                .partner(partner)
                .name(dto.getName())
                .position(dto.getPosition())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .isPrimary(dto.getIsPrimary() != null ? dto.getIsPrimary() : false)
                .build();
        return toDto(contactRepository.save(contact));
    }

    public List<PartnerContactDto> getByPartnerId(UUID partnerId) {
        return contactRepository.findByPartnerId(partnerId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PartnerContactDto getById(UUID id) {
        PartnerContact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PartnerContact", "id", id));
        return toDto(contact);
    }

    @Transactional
    public void delete(UUID id) {
        if (!contactRepository.existsById(id)) {
            throw new ResourceNotFoundException("PartnerContact", "id", id);
        }
        contactRepository.deleteById(id);
    }

    private PartnerContactDto toDto(PartnerContact c) {
        return PartnerContactDto.builder()
                .id(c.getId())
                .partnerId(c.getPartner().getId())
                .name(c.getName())
                .position(c.getPosition())
                .email(c.getEmail())
                .phone(c.getPhone())
                .isPrimary(c.getIsPrimary())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
