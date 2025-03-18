package com.example.gamel.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "ProductReviews")
public class ProductReview {

    @NotNull(message = "Product ID is required")
    @DynamoDBHashKey(attributeName = "productId")
    private Long productId;

    @DynamoDBRangeKey(attributeName = "reviewId")
    private Long reviewId;

    @NotNull(message = "User ID is required")
    @DynamoDBAttribute(attributeName = "userId")
    private Long userId;

    @NotNull(message = "Rating is required")
    @DynamoDBAttribute(attributeName = "rating")
    private Long rating;

    @DynamoDBAttribute(attributeName = "reviewText")
    private String reviewText;

    @DynamoDBAttribute(attributeName = "content")
    private String content;

    @DynamoDBAttribute(attributeName = "createdAt")
    private String createdAt;
}