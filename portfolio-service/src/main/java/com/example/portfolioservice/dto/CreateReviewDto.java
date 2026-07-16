package com.example.portfolioservice.dto;

import com.example.portfolioservice.model.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewDto {
    private UUID fromUserId;
    private String content;
    private Integer rating;
    private Review.ReviewType type;
    private UUID relatedEntityId;
}
