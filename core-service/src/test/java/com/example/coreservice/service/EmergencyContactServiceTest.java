package com.example.coreservice.service;

import com.example.coreservice.dto.EmergencyContactDto;
import com.example.coreservice.model.entity.EmergencyContact;
import com.example.coreservice.model.entity.User;
import com.example.coreservice.model.enums.RelationshipType;
import com.example.coreservice.repository.EmergencyContactRepository;
import com.example.coreservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmergencyContactServiceTest {

    @Mock
    private EmergencyContactRepository emergencyContactRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmergencyContactService emergencyContactService;

    private UUID userId;
    private EmergencyContact testContact;
    private EmergencyContactDto testContactDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder().id(userId).email("test@example.com").build();
        testContact = EmergencyContact.builder()
                .userId(userId)
                .contactName("John Doe")
                .contactPhone("+1234567890")
                .relationship(RelationshipType.PARENT)
                .build();
        testContactDto = EmergencyContactDto.builder()
                .userId(userId)
                .contactName("John Doe")
                .contactPhone("+1234567890")
                .relationship("PARENT")
                .build();
    }

    @Test
    void getAllEmergencyContacts_ReturnsList() {
        when(emergencyContactRepository.findAll()).thenReturn(List.of(testContact));

        List<EmergencyContactDto> result = emergencyContactService.getAllEmergencyContacts();

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getContactName());
        assertEquals("PARENT", result.get(0).getRelationship());
        verify(emergencyContactRepository, times(1)).findAll();
    }

    @Test
    void getAllEmergencyContacts_ReturnsEmptyList() {
        when(emergencyContactRepository.findAll()).thenReturn(Collections.emptyList());

        List<EmergencyContactDto> result = emergencyContactService.getAllEmergencyContacts();

        assertTrue(result.isEmpty());
    }

    @Test
    void getEmergencyContactById_WhenExists_ReturnsContact() {
        when(emergencyContactRepository.findById(userId)).thenReturn(Optional.of(testContact));

        EmergencyContactDto result = emergencyContactService.getEmergencyContactById(userId);

        assertNotNull(result);
        assertEquals("John Doe", result.getContactName());
        assertEquals("+1234567890", result.getContactPhone());
    }

    @Test
    void getEmergencyContactById_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        when(emergencyContactRepository.findById(id)).thenReturn(Optional.empty());

        EmergencyContactDto result = emergencyContactService.getEmergencyContactById(id);

        assertNull(result);
    }

    @Test
    void createEmergencyContact_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(emergencyContactRepository.save(any(EmergencyContact.class))).thenReturn(testContact);

        EmergencyContactDto result = emergencyContactService.createEmergencyContact(testContactDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getContactName());
        verify(emergencyContactRepository, times(1)).save(any(EmergencyContact.class));
    }

    @Test
    void createEmergencyContact_UserNotFound_ThrowsException() {
        UUID nonExistentUserId = UUID.randomUUID();
        EmergencyContactDto dto = EmergencyContactDto.builder()
                .userId(nonExistentUserId)
                .contactName("Jane")
                .contactPhone("+0000000000")
                .relationship("SIBLING")
                .build();

        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> emergencyContactService.createEmergencyContact(dto));
    }

    @Test
    void updateEmergencyContact_WhenExists_ReturnsUpdated() {
        when(emergencyContactRepository.existsById(userId)).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(emergencyContactRepository.save(any(EmergencyContact.class))).thenReturn(testContact);

        EmergencyContactDto result = emergencyContactService.updateEmergencyContact(userId, testContactDto);

        assertNotNull(result);
        assertEquals("John Doe", result.getContactName());
        verify(emergencyContactRepository, times(1)).save(any(EmergencyContact.class));
    }

    @Test
    void updateEmergencyContact_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        when(emergencyContactRepository.existsById(id)).thenReturn(false);

        EmergencyContactDto result = emergencyContactService.updateEmergencyContact(id, testContactDto);

        assertNull(result);
        verify(emergencyContactRepository, never()).save(any(EmergencyContact.class));
    }

    @Test
    void deleteEmergencyContact_CallsRepository() {
        doNothing().when(emergencyContactRepository).deleteById(userId);

        emergencyContactService.deleteEmergencyContact(userId);

        verify(emergencyContactRepository, times(1)).deleteById(userId);
    }
}
