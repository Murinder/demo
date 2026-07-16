package com.example.portfolioservice.service;

import com.example.portfolioservice.dto.PortfolioDto;
import com.example.portfolioservice.model.Portfolio;
import com.example.portfolioservice.repository.PortfolioRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void createPortfolio_NewUser_CreatesPortfolio() {
        UUID userId = UUID.randomUUID();
        when(portfolioRepository.existsById(userId)).thenReturn(false);
        when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(inv -> inv.getArgument(0));

        PortfolioDto result = portfolioService.createPortfolio(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    void createPortfolio_ExistingUser_ReturnsExisting() {
        UUID userId = UUID.randomUUID();
        Portfolio existing = Portfolio.builder().userId(userId).build();
        when(portfolioRepository.existsById(userId)).thenReturn(true);
        when(portfolioRepository.findById(userId)).thenReturn(Optional.of(existing));

        PortfolioDto result = portfolioService.createPortfolio(userId);

        assertNotNull(result);
        verify(portfolioRepository, never()).save(any());
    }

    @Test
    void getPortfolioByUserId_NotFound_ThrowsException() {
        UUID userId = UUID.randomUUID();
        when(portfolioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> portfolioService.getPortfolioByUserId(userId));
    }

    @Test
    void getPortfolioByUserId_Found_ReturnsDtoWithEmptyCollections() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = Portfolio.builder()
                .userId(userId)
                .visibilitySettings("{\"public\": true}")
                .build();
        when(portfolioRepository.findById(userId)).thenReturn(Optional.of(portfolio));

        PortfolioDto result = portfolioService.getPortfolioByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("{\"public\": true}", result.getVisibilitySettings());
        assertTrue(result.getAchievements().isEmpty());
        assertTrue(result.getSkills().isEmpty());
        assertTrue(result.getReviews().isEmpty());
    }

    @Test
    void updateVisibility_ExistingPortfolio_UpdatesAndReturns() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = Portfolio.builder().userId(userId).build();
        when(portfolioRepository.findById(userId)).thenReturn(Optional.of(portfolio));
        when(portfolioRepository.save(any(Portfolio.class))).thenAnswer(inv -> inv.getArgument(0));

        PortfolioDto result = portfolioService.updateVisibility(userId, "{\"achievements\": false}");

        assertNotNull(result);
        assertEquals("{\"achievements\": false}", result.getVisibilitySettings());
        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    void updateVisibility_NotFound_ThrowsException() {
        UUID userId = UUID.randomUUID();
        when(portfolioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> portfolioService.updateVisibility(userId, "{\"public\": true}"));
    }
}
