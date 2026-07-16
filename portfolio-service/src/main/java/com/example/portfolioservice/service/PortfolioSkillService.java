package com.example.portfolioservice.service;

import com.example.portfolioservice.dto.CreateSkillDto;
import com.example.portfolioservice.dto.PortfolioSkillDto;
import com.example.portfolioservice.model.Portfolio;
import com.example.portfolioservice.model.PortfolioSkill;
import com.example.portfolioservice.repository.PortfolioRepository;
import com.example.portfolioservice.repository.PortfolioSkillRepository;
import com.example.sharedlib.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PortfolioSkillService {

    private final PortfolioSkillRepository skillRepository;
    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true)
    public List<PortfolioSkillDto> getSkillsByUserId(UUID userId) {
        return skillRepository.findByPortfolioUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public PortfolioSkillDto addSkill(UUID userId, CreateSkillDto dto) {
        Portfolio portfolio = portfolioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found for user: " + userId));

        PortfolioSkill skill = PortfolioSkill.builder()
                .portfolio(portfolio)
                .name(dto.getName())
                .level(dto.getLevel())
                .build();

        return mapToDto(skillRepository.save(skill));
    }

    public PortfolioSkillDto updateSkillLevel(UUID skillId, Integer newLevel) {
        PortfolioSkill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));
        skill.setLevel(newLevel);
        return mapToDto(skillRepository.save(skill));
    }

    public PortfolioSkillDto verifySkill(UUID skillId, UUID verifiedBy) {
        PortfolioSkill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));
        skill.setVerificationStatus(PortfolioSkill.VerificationStatus.VERIFIED);
        skill.setVerifiedBy(verifiedBy);
        skill.setVerifiedAt(OffsetDateTime.now());
        return mapToDto(skillRepository.save(skill));
    }

    @Transactional(readOnly = true)
    public List<PortfolioSkillDto> searchBySkill(String skillName, Integer minLevel) {
        return skillRepository.findByNameAndLevelGreaterThanEqual(skillName, minLevel).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void deleteSkill(UUID skillId) {
        skillRepository.deleteById(skillId);
    }

    private PortfolioSkillDto mapToDto(PortfolioSkill s) {
        return PortfolioSkillDto.builder()
                .id(s.getId())
                .portfolioId(s.getPortfolio().getUserId())
                .name(s.getName())
                .level(s.getLevel())
                .verificationStatus(s.getVerificationStatus())
                .verifiedBy(s.getVerifiedBy())
                .verifiedAt(s.getVerifiedAt())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
