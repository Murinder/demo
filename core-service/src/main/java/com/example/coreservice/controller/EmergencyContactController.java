package com.example.coreservice.controller;

import com.example.coreservice.dto.EmergencyContactDto;
import com.example.coreservice.service.EmergencyContactService;
import com.example.sharedlib.security.AuthenticatedOnly;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/emergency-contacts")
@RequiredArgsConstructor
@AuthenticatedOnly
@Tag(name = "Emergency Contacts", description = "Emergency contact management APIs")
public class EmergencyContactController {
    private final EmergencyContactService emergencyContactService;

    @GetMapping
    @Operation(summary = "Get all emergency contacts")
    public ResponseEntity<List<EmergencyContactDto>> getAll() {
        return ResponseEntity.ok(emergencyContactService.getAllEmergencyContacts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get emergency contact by id")
    public ResponseEntity<EmergencyContactDto> getById(@PathVariable UUID id) {
        EmergencyContactDto emergencyContactDto = emergencyContactService.getEmergencyContactById(id);
        if (emergencyContactDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(emergencyContactDto);
    }

    @PostMapping
    @Operation(summary = "Create a new emergency contact")
    public ResponseEntity<EmergencyContactDto> create(@RequestBody EmergencyContactDto emergencyContactDto) {
        return ResponseEntity.ok(emergencyContactService.createEmergencyContact(emergencyContactDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an emergency contact")
    public ResponseEntity<EmergencyContactDto> update(@PathVariable UUID id, @RequestBody EmergencyContactDto emergencyContactDto) {
        EmergencyContactDto updatedEmergencyContact = emergencyContactService.updateEmergencyContact(id, emergencyContactDto);
        if (updatedEmergencyContact == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedEmergencyContact);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an emergency contact")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        emergencyContactService.deleteEmergencyContact(id);
        return ResponseEntity.noContent().build();
    }
}