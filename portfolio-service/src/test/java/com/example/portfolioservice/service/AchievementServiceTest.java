package com.example.portfolioservice.service;

import com.example.portfolioservice.dto.AchievementDto;
import com.example.portfolioservice.dto.CreateAchievementDto;
import com.example.portfolioservice.model.Achievement;
import com.example.portfolioservice.model.Portfolio;
import com.example.portfolioservice.repository.AchievementRepository;
import com.example.portfolioservice.repository.PortfolioRepository;
import com.example.sharedlib.event.EventPublisher;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;
    @Mock
    private PortfolioRepository portfolioRepository;
    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private AchievementService achievementService;

    @Test
    void createAchievement_PublishesEvent() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = Portfolio.builder().userId(userId).build();
        CreateAchievementDto dto = new CreateAchievementDto();
        dto.setType(Achievement.AchievementType.PROJECT);
        dto.setTitle("Completed Project X");

        when(portfolioRepository.findById(userId)).thenReturn(Optional.of(portfolio));
        when(achievementRepository.save(any(Achievement.class))).thenAnswer(inv -> {
            Achievement a = inv.getArgument(0);
            a.setId(UUID.randomUUID());
            return a;
        });

        AchievementDto result = achievementService.createAchievement(userId, dto);

        assertNotNull(result);
        assertEquals("Completed Project X", result.getTitle());
        verify(eventPublisher).publish(eq("etsopy.portfolio"), eq("portfolio.achievement_added"), any());
    }

    @Test
    void getAchievementsByUserId_ReturnsListOfDtos() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = Portfolio.builder().userId(userId).build();
        Achievement a1 = Achievement.builder()
                .id(UUID.randomUUID())
                .portfolio(portfolio)
                .type(Achievement.AchievementType.CERTIFICATE)
                .title("Java Cert")
                .build();
        Achievement a2 = Achievement.builder()
                .id(UUID.randomUUID())
                .portfolio(portfolio)
                .type(Achievement.AchievementType.PUBLICATION)
                .title("Research Paper")
                .build();

        when(achievementRepository.findByPortfolioUserId(userId)).thenReturn(List.of(a1, a2));

        List<AchievementDto> result = achievementService.getAchievementsByUserId(userId);

        assertEquals(2, result.size());
        assertEquals("Java Cert", result.get(0).getTitle());
        assertEquals("Research Paper", result.get(1).getTitle());
    }

    @Test
    void getAchievementsByUserId_NoAchievements_ReturnsEmptyList() {
        UUID userId = UUID.randomUUID();
        when(achievementRepository.findByPortfolioUserId(userId)).thenReturn(Collections.emptyList());

        List<AchievementDto> result = achievementService.getAchievementsByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createAchievement_PortfolioNotFound_ThrowsException() {
        UUID userId = UUID.randomUUID();
        CreateAchievementDto dto = new CreateAchievementDto();
        dto.setType(Achievement.AchievementType.GRANT);
        dto.setTitle("Research Grant");

        when(portfolioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(com.example.sharedlib.exception.ResourceNotFoundException.class,
                () -> achievementService.createAchievement(userId, dto));
        verify(achievementRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any(), any(), any());
    }

    @Test
    void deleteAchievement_CallsRepositoryDeleteById() {
        UUID achievementId = UUID.randomUUID();

        achievementService.deleteAchievement(achievementId);

        verify(achievementRepository).deleteById(achievementId);
    }
}
