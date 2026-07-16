package com.example.portfolioservice.service;

import com.example.portfolioservice.dto.AchievementDto;
import com.example.portfolioservice.dto.CreateAchievementDto;
import com.example.portfolioservice.model.Achievement;
import com.example.portfolioservice.model.Portfolio;
import com.example.portfolioservice.repository.AchievementRepository;
import com.example.portfolioservice.repository.PortfolioRepository;
import com.example.sharedlib.config.RabbitMqAutoConfiguration;
import com.example.sharedlib.event.EventPublisher;
import com.example.sharedlib.event.PortfolioEvent;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final PortfolioRepository portfolioRepository;
    private final EventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<AchievementDto> getAchievementsByUserId(UUID userId) {
        return achievementRepository.findByPortfolioUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public AchievementDto createAchievement(UUID userId, CreateAchievementDto dto) {
        Portfolio portfolio = portfolioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found for user: " + userId));

        Achievement achievement = Achievement.builder()
                .portfolio(portfolio)
                .type(dto.getType())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .date(dto.getDate())
                .proofDocument(dto.getProofDocument())
                .isExternal(dto.getIsExternal() != null ? dto.getIsExternal() : false)
                .issuer(dto.getIssuer())
                .build();

        Achievement saved = achievementRepository.save(achievement);

        // Publish portfolio.achievement_added
        PortfolioEvent event = PortfolioEvent.builder()
                .userId(userId)
                .achievementType(dto.getType().name())
                .title(dto.getTitle())
                .build();
        event.init("portfolio-service");
        eventPublisher.publish(RabbitMqAutoConfiguration.PORTFOLIO_EXCHANGE, "portfolio.achievement_added", event);

        return mapToDto(saved);
    }

    public void deleteAchievement(UUID achievementId) {
        achievementRepository.deleteById(achievementId);
    }

    private AchievementDto mapToDto(Achievement a) {
        return AchievementDto.builder()
                .id(a.getId())
                .portfolioId(a.getPortfolio().getUserId())
                .type(a.getType())
                .title(a.getTitle())
                .description(a.getDescription())
                .date(a.getDate())
                .proofDocument(a.getProofDocument())
                .isExternal(a.getIsExternal())
                .issuer(a.getIssuer())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
