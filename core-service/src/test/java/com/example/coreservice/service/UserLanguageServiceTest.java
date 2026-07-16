package com.example.coreservice.service;

import com.example.coreservice.model.dto.UserLanguageDto;
import com.example.coreservice.model.entity.UserLanguage;
import com.example.coreservice.model.entity.UserLanguageId;
import com.example.coreservice.model.enums.LanguageProficiency;
import com.example.coreservice.repository.UserLanguageRepository;
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
class UserLanguageServiceTest {

    @Mock
    private UserLanguageRepository userLanguageRepository;

    @InjectMocks
    private UserLanguageService userLanguageService;

    private UUID userId;
    private String language;
    private UserLanguageId compositeId;
    private UserLanguage testUserLanguage;
    private UserLanguageDto testUserLanguageDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        language = "English";
        compositeId = new UserLanguageId(userId, language);

        testUserLanguage = UserLanguage.builder()
                .id(compositeId)
                .proficiency(LanguageProficiency.FLUENT)
                .build();

        testUserLanguageDto = new UserLanguageDto(userId, language, LanguageProficiency.FLUENT);
    }

    @Test
    void getAllUserLanguages_ReturnsList() {
        when(userLanguageRepository.findAll()).thenReturn(List.of(testUserLanguage));

        List<UserLanguageDto> result = userLanguageService.getAllUserLanguages();

        assertEquals(1, result.size());
        assertEquals("English", result.get(0).language());
        assertEquals(LanguageProficiency.FLUENT, result.get(0).proficiency());
        verify(userLanguageRepository, times(1)).findAll();
    }

    @Test
    void getAllUserLanguages_ReturnsEmptyList() {
        when(userLanguageRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserLanguageDto> result = userLanguageService.getAllUserLanguages();

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserLanguageById_WhenExists_ReturnsLanguage() {
        when(userLanguageRepository.findById(compositeId)).thenReturn(Optional.of(testUserLanguage));

        UserLanguageDto result = userLanguageService.getUserLanguageById(userId, language);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals("English", result.language());
        assertEquals(LanguageProficiency.FLUENT, result.proficiency());
    }

    @Test
    void getUserLanguageById_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        UserLanguageId nonExistentId = new UserLanguageId(id, "French");
        when(userLanguageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        UserLanguageDto result = userLanguageService.getUserLanguageById(id, "French");

        assertNull(result);
    }

    @Test
    void createUserLanguage_Success() {
        when(userLanguageRepository.save(any(UserLanguage.class))).thenReturn(testUserLanguage);

        UserLanguageDto result = userLanguageService.createUserLanguage(testUserLanguageDto);

        assertNotNull(result);
        assertEquals("English", result.language());
        assertEquals(LanguageProficiency.FLUENT, result.proficiency());
        verify(userLanguageRepository, times(1)).save(any(UserLanguage.class));
    }

    @Test
    void updateUserLanguage_WhenExists_ReturnsUpdated() {
        when(userLanguageRepository.existsById(compositeId)).thenReturn(true);
        when(userLanguageRepository.save(any(UserLanguage.class))).thenReturn(testUserLanguage);

        UserLanguageDto result = userLanguageService.updateUserLanguage(userId, language, testUserLanguageDto);

        assertNotNull(result);
        assertEquals("English", result.language());
        verify(userLanguageRepository, times(1)).save(any(UserLanguage.class));
    }

    @Test
    void updateUserLanguage_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        UserLanguageId nonExistentId = new UserLanguageId(id, "German");
        when(userLanguageRepository.existsById(nonExistentId)).thenReturn(false);

        UserLanguageDto result = userLanguageService.updateUserLanguage(id, "German", testUserLanguageDto);

        assertNull(result);
        verify(userLanguageRepository, never()).save(any(UserLanguage.class));
    }

    @Test
    void deleteUserLanguage_CallsRepository() {
        doNothing().when(userLanguageRepository).deleteById(compositeId);

        userLanguageService.deleteUserLanguage(userId, language);

        verify(userLanguageRepository, times(1)).deleteById(compositeId);
    }
}
