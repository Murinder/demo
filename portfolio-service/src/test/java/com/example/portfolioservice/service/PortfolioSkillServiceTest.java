package com.example.portfolioservice.service;

import com.example.portfolioservice.dto.CreateSkillDto;
import com.example.portfolioservice.dto.PortfolioSkillDto;
import com.example.portfolioservice.model.Portfolio;
import com.example.portfolioservice.model.PortfolioSkill;
import com.example.portfolioservice.repository.PortfolioRepository;
import com.example.portfolioservice.repository.PortfolioSkillRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
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
class PortfolioSkillServiceTest {

    @Mock
    private PortfolioSkillRepository skillRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioSkillService portfolioSkillService;

    @Test
    void getSkillsByUserId_ReturnsListOfSkills() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = Portfolio.builder().userId(userId).build();
        PortfolioSkill skill = PortfolioSkill.builder()
                .id(UUID.randomUUID())
                .portfolio(portfolio)
                .name("Java")
                .level(8)
                .verificationStatus(PortfolioSkill.VerificationStatus.PENDING)
                .build();

        when(skillRepository.findByPortfolioUserId(userId)).thenReturn(List.of(skill));

        List<PortfolioSkillDto> result = portfolioSkillService.getSkillsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getName());
        assertEquals(8, result.get(0).getLevel());
    }

    @Test
    void getSkillsByUserId_NoSkills_ReturnsEmptyList() {
        UUID userId = UUID.randomUUID();
        when(skillRepository.findByPortfolioUserId(userId)).thenReturn(Collections.emptyList());

        List<PortfolioSkillDto> result = portfolioSkillService.getSkillsByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addSkill_ValidData_ReturnsDto() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = Portfolio.builder().userId(userId).build();
        CreateSkillDto dto = new CreateSkillDto("Spring Boot", 7);

        when(portfolioRepository.findById(userId)).thenReturn(Optional.of(portfolio));
        when(skillRepository.save(any(PortfolioSkill.class))).thenAnswer(inv -> {
            PortfolioSkill s = inv.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        PortfolioSkillDto result = portfolioSkillService.addSkill(userId, dto);

        assertNotNull(result);
        assertEquals("Spring Boot", result.getName());
        assertEquals(7, result.getLevel());
        verify(skillRepository).save(any(PortfolioSkill.class));
    }

    @Test
    void addSkill_PortfolioNotFound_ThrowsException() {
        UUID userId = UUID.randomUUID();
        CreateSkillDto dto = new CreateSkillDto("Python", 5);

        when(portfolioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> portfolioSkillService.addSkill(userId, dto));
        verify(skillRepository, never()).save(any());
    }

    @Test
    void updateSkillLevel_ExistingSkill_UpdatesLevel() {
        UUID skillId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = Portfolio.builder().userId(userId).build();
        PortfolioSkill skill = PortfolioSkill.builder()
                .id(skillId)
                .portfolio(portfolio)
                .name("Docker")
                .level(3)
                .build();

        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.save(any(PortfolioSkill.class))).thenAnswer(inv -> inv.getArgument(0));

        PortfolioSkillDto result = portfolioSkillService.updateSkillLevel(skillId, 6);

        assertEquals(6, result.getLevel());
        assertEquals("Docker", result.getName());
    }

    @Test
    void updateSkillLevel_NotFound_ThrowsException() {
        UUID skillId = UUID.randomUUID();
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> portfolioSkillService.updateSkillLevel(skillId, 5));
    }

    @Test
    void verifySkill_SetsVerifiedStatus() {
        UUID skillId = UUID.randomUUID();
        UUID verifiedBy = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = Portfolio.builder().userId(userId).build();
        PortfolioSkill skill = PortfolioSkill.builder()
                .id(skillId)
                .portfolio(portfolio)
                .name("Kubernetes")
                .level(5)
                .verificationStatus(PortfolioSkill.VerificationStatus.PENDING)
                .build();

        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.save(any(PortfolioSkill.class))).thenAnswer(inv -> inv.getArgument(0));

        PortfolioSkillDto result = portfolioSkillService.verifySkill(skillId, verifiedBy);

        assertEquals(PortfolioSkill.VerificationStatus.VERIFIED, result.getVerificationStatus());
        assertEquals(verifiedBy, result.getVerifiedBy());
        assertNotNull(result.getVerifiedAt());
    }

    @Test
    void searchBySkill_ReturnsMatchingSkills() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = Portfolio.builder().userId(userId).build();
        PortfolioSkill skill = PortfolioSkill.builder()
                .id(UUID.randomUUID())
                .portfolio(portfolio)
                .name("Java")
                .level(9)
                .build();

        when(skillRepository.findByNameAndLevelGreaterThanEqual("Java", 7)).thenReturn(List.of(skill));

        List<PortfolioSkillDto> result = portfolioSkillService.searchBySkill("Java", 7);

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getName());
        assertTrue(result.get(0).getLevel() >= 7);
    }

    @Test
    void deleteSkill_CallsRepositoryDeleteById() {
        UUID skillId = UUID.randomUUID();

        portfolioSkillService.deleteSkill(skillId);

        verify(skillRepository).deleteById(skillId);
    }
}
