package com.example.portfolioservice.service;

import com.example.portfolioservice.dto.CreateReviewDto;
import com.example.portfolioservice.dto.ReviewDto;
import com.example.portfolioservice.model.Portfolio;
import com.example.portfolioservice.model.Review;
import com.example.portfolioservice.repository.PortfolioRepository;
import com.example.portfolioservice.repository.ReviewRepository;
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
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void getReviewsByUserId_ReturnsListOfReviews() {
        UUID userId = UUID.randomUUID();
        Portfolio portfolio = Portfolio.builder().userId(userId).build();
        Review review = Review.builder()
                .id(UUID.randomUUID())
                .portfolio(portfolio)
                .fromUserId(UUID.randomUUID())
                .content("Great teamwork!")
                .rating(5)
                .type(Review.ReviewType.PROJECT)
                .build();

        when(reviewRepository.findByPortfolioUserId(userId)).thenReturn(List.of(review));

        List<ReviewDto> result = reviewService.getReviewsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals("Great teamwork!", result.get(0).getContent());
        assertEquals(5, result.get(0).getRating());
        assertEquals(Review.ReviewType.PROJECT, result.get(0).getType());
    }

    @Test
    void getReviewsByUserId_NoReviews_ReturnsEmptyList() {
        UUID userId = UUID.randomUUID();
        when(reviewRepository.findByPortfolioUserId(userId)).thenReturn(Collections.emptyList());

        List<ReviewDto> result = reviewService.getReviewsByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createReview_ValidData_ReturnsSavedReview() {
        UUID userId = UUID.randomUUID();
        UUID fromUserId = UUID.randomUUID();
        UUID relatedEntityId = UUID.randomUUID();
        Portfolio portfolio = Portfolio.builder().userId(userId).build();

        CreateReviewDto dto = new CreateReviewDto();
        dto.setFromUserId(fromUserId);
        dto.setContent("Excellent mentoring skills");
        dto.setRating(4);
        dto.setType(Review.ReviewType.MENTORSHIP);
        dto.setRelatedEntityId(relatedEntityId);

        when(portfolioRepository.findById(userId)).thenReturn(Optional.of(portfolio));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        ReviewDto result = reviewService.createReview(userId, dto);

        assertNotNull(result);
        assertEquals("Excellent mentoring skills", result.getContent());
        assertEquals(4, result.getRating());
        assertEquals(Review.ReviewType.MENTORSHIP, result.getType());
        assertEquals(fromUserId, result.getFromUserId());
        assertEquals(relatedEntityId, result.getRelatedEntityId());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_PortfolioNotFound_ThrowsException() {
        UUID userId = UUID.randomUUID();
        CreateReviewDto dto = new CreateReviewDto();
        dto.setFromUserId(UUID.randomUUID());
        dto.setContent("Good work");
        dto.setRating(3);
        dto.setType(Review.ReviewType.EVENT);

        when(portfolioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.createReview(userId, dto));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void deleteReview_CallsRepositoryDeleteById() {
        UUID reviewId = UUID.randomUUID();

        reviewService.deleteReview(reviewId);

        verify(reviewRepository).deleteById(reviewId);
    }
}
