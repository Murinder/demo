package com.example.portfolioservice.service;

import com.example.portfolioservice.dto.CreateReviewDto;
import com.example.portfolioservice.dto.ReviewDto;
import com.example.portfolioservice.model.Portfolio;
import com.example.portfolioservice.model.Review;
import com.example.portfolioservice.repository.PortfolioRepository;
import com.example.portfolioservice.repository.ReviewRepository;
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
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final PortfolioRepository portfolioRepository;

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsByUserId(UUID userId) {
        return reviewRepository.findByPortfolioUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ReviewDto createReview(UUID userId, CreateReviewDto dto) {
        Portfolio portfolio = portfolioRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found for user: " + userId));

        Review review = Review.builder()
                .portfolio(portfolio)
                .fromUserId(dto.getFromUserId())
                .content(dto.getContent())
                .rating(dto.getRating())
                .type(dto.getType())
                .relatedEntityId(dto.getRelatedEntityId())
                .build();

        return mapToDto(reviewRepository.save(review));
    }

    public void deleteReview(UUID reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    private ReviewDto mapToDto(Review r) {
        return ReviewDto.builder()
                .id(r.getId())
                .portfolioId(r.getPortfolio().getUserId())
                .fromUserId(r.getFromUserId())
                .content(r.getContent())
                .rating(r.getRating())
                .type(r.getType())
                .relatedEntityId(r.getRelatedEntityId())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
