package com.example.coreservice.service;

import com.example.coreservice.model.dto.UserSkillDto;
import com.example.coreservice.model.entity.UserSkill;
import com.example.coreservice.model.entity.UserSkillId;
import com.example.coreservice.repository.UserSkillRepository;
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
class UserSkillServiceTest {

    @Mock
    private UserSkillRepository userSkillRepository;

    @InjectMocks
    private UserSkillService userSkillService;

    private UUID userId;
    private String skillName;
    private UserSkillId compositeId;
    private UserSkill testUserSkill;
    private UserSkillDto testUserSkillDto;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        skillName = "Java";
        compositeId = new UserSkillId(userId, skillName);

        testUserSkill = UserSkill.builder()
                .id(compositeId)
                .level(5)
                .verified(true)
                .build();

        testUserSkillDto = new UserSkillDto(userId, skillName, 5, true);
    }

    @Test
    void getAllUserSkills_ReturnsList() {
        when(userSkillRepository.findAll()).thenReturn(List.of(testUserSkill));

        List<UserSkillDto> result = userSkillService.getAllUserSkills();

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).skillName());
        assertEquals(5, result.get(0).level());
        assertTrue(result.get(0).verified());
        verify(userSkillRepository, times(1)).findAll();
    }

    @Test
    void getAllUserSkills_ReturnsEmptyList() {
        when(userSkillRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserSkillDto> result = userSkillService.getAllUserSkills();

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserSkillById_WhenExists_ReturnsSkill() {
        when(userSkillRepository.findById(compositeId)).thenReturn(Optional.of(testUserSkill));

        UserSkillDto result = userSkillService.getUserSkillById(userId, skillName);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals("Java", result.skillName());
        assertEquals(5, result.level());
        assertTrue(result.verified());
    }

    @Test
    void getUserSkillById_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        UserSkillId nonExistentId = new UserSkillId(id, "Python");
        when(userSkillRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        UserSkillDto result = userSkillService.getUserSkillById(id, "Python");

        assertNull(result);
    }

    @Test
    void createUserSkill_Success() {
        when(userSkillRepository.save(any(UserSkill.class))).thenReturn(testUserSkill);

        UserSkillDto result = userSkillService.createUserSkill(testUserSkillDto);

        assertNotNull(result);
        assertEquals("Java", result.skillName());
        assertEquals(5, result.level());
        assertTrue(result.verified());
        verify(userSkillRepository, times(1)).save(any(UserSkill.class));
    }

    @Test
    void updateUserSkill_WhenExists_ReturnsUpdated() {
        when(userSkillRepository.existsById(compositeId)).thenReturn(true);
        when(userSkillRepository.save(any(UserSkill.class))).thenReturn(testUserSkill);

        UserSkillDto result = userSkillService.updateUserSkill(userId, skillName, testUserSkillDto);

        assertNotNull(result);
        assertEquals("Java", result.skillName());
        verify(userSkillRepository, times(1)).save(any(UserSkill.class));
    }

    @Test
    void updateUserSkill_WhenNotExists_ReturnsNull() {
        UUID id = UUID.randomUUID();
        UserSkillId nonExistentId = new UserSkillId(id, "Go");
        when(userSkillRepository.existsById(nonExistentId)).thenReturn(false);

        UserSkillDto result = userSkillService.updateUserSkill(id, "Go", testUserSkillDto);

        assertNull(result);
        verify(userSkillRepository, never()).save(any(UserSkill.class));
    }

    @Test
    void deleteUserSkill_CallsRepository() {
        doNothing().when(userSkillRepository).deleteById(compositeId);

        userSkillService.deleteUserSkill(userId, skillName);

        verify(userSkillRepository, times(1)).deleteById(compositeId);
    }
}
