package com.example.portfolioservice.dto;

import com.example.portfolioservice.model.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private UUID id;
    private UUID portfolioId;
    private UUID fromUserId;
    private String content;
    private Integer rating;
    private Review.ReviewType type;
    private UUID relatedEntityId;
    private OffsetDateTime createdAt;
}
