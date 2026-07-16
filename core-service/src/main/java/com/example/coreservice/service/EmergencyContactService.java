package com.example.coreservice.service;

import com.example.coreservice.dto.EmergencyContactDto;
import com.example.coreservice.model.entity.EmergencyContact;
import com.example.coreservice.model.entity.User;
import com.example.coreservice.repository.EmergencyContactRepository;
import com.example.coreservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmergencyContactService {

    private final EmergencyContactRepository emergencyContactRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<EmergencyContactDto> getAllEmergencyContacts() {
        return emergencyContactRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EmergencyContactDto getEmergencyContactById(UUID id) {
        return emergencyContactRepository.findById(id)
                .map(this::mapToDto)
                .orElse(null); // Or throw an exception
    }

    public EmergencyContactDto createEmergencyContact(EmergencyContactDto emergencyContactDto) {
        EmergencyContact emergencyContact = mapToEntity(emergencyContactDto);
        return mapToDto(emergencyContactRepository.save(emergencyContact));
    }

    public EmergencyContactDto updateEmergencyContact(UUID id, EmergencyContactDto emergencyContactDto) {
        if (!emergencyContactRepository.existsById(id)) {
            return null; // Or throw an exception
        }
        EmergencyContact emergencyContact = mapToEntity(emergencyContactDto);
        emergencyContact.setUserId(id);
        return mapToDto(emergencyContactRepository.save(emergencyContact));
    }

    public void deleteEmergencyContact(UUID id) {
        emergencyContactRepository.deleteById(id);
    }

    private EmergencyContactDto mapToDto(EmergencyContact emergencyContact) {
        return EmergencyContactDto.builder()
                .userId(emergencyContact.getUserId())
                .contactName(emergencyContact.getContactName())
                .contactPhone(emergencyContact.getContactPhone())
                .relationship(emergencyContact.getRelationship().name())
                .build();
    }

    private EmergencyContact mapToEntity(EmergencyContactDto emergencyContactDto) {
        User user = userRepository.findById(emergencyContactDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")); // Or a more specific exception
        return EmergencyContact.builder()
                .userId(emergencyContactDto.getUserId())
                .contactName(emergencyContactDto.getContactName())
                .contactPhone(emergencyContactDto.getContactPhone())
                .relationship(com.example.coreservice.model.enums.RelationshipType.valueOf(emergencyContactDto.getRelationship()))
                .build();
    }
}