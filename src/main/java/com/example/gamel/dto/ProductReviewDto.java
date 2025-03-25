package com.example.gamel.dto;

import com.example.gamel.entity.dynamo.ProductReview;
import lombok.Data;

@Data
public class ProductReviewDto {
    private Long productId;
    private Long reviewId;
    private Long userId;
    private Long rating;
    private String reviewText;
    private String createdAt;

    public static ProductReviewDto fromEntity(ProductReview review) {
        ProductReviewDto dto = new ProductReviewDto();
        dto.setProductId(review.getProductId());
        dto.setReviewId(review.getReviewId());
        dto.setUserId(review.getUserId());
        dto.setRating(review.getRating());
        dto.setReviewText(review.getReviewText());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}