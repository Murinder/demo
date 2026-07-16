package com.example.portfolioservice.service;

import com.example.portfolioservice.dto.*;
import com.example.portfolioservice.model.Portfolio;
import com.example.portfolioservice.repository.PortfolioRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true)
    public PortfolioDto getPortfolioByUserId(UUID userId) {
        Portfolio portfolio = portfolioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found for user: " + userId));
        return mapToDto(portfolio);
    }

    public PortfolioDto createPortfolio(UUID userId) {
        if (portfolioRepository.existsById(userId)) {
            log.info("Portfolio already exists for user: {}", userId);
            return getPortfolioByUserId(userId);
        }
        Portfolio portfolio = Portfolio.builder()
                .userId(userId)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        return mapToDto(portfolioRepository.save(portfolio));
    }

    public PortfolioDto updateVisibility(UUID userId, String visibilitySettings) {
        Portfolio portfolio = portfolioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found for user: " + userId));
        portfolio.setVisibilitySettings(visibilitySettings);
        portfolio.setUpdatedAt(OffsetDateTime.now());
        return mapToDto(portfolioRepository.save(portfolio));
    }

    private PortfolioDto mapToDto(Portfolio portfolio) {
        return PortfolioDto.builder()
                .userId(portfolio.getUserId())
                .visibilitySettings(portfolio.getVisibilitySettings())
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .achievements(Collections.emptyList())
                .skills(Collections.emptyList())
                .reviews(Collections.emptyList())
                .build();
    }
}
