package com.example.gamel.repository;

import com.example.gamel.entity.dynamo.ProductReview;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.List;

public interface ProductReviewRepository {

    void save(ProductReview review);

    List<ProductReview> findByProductId(Long productId);

    Page<ProductReview> findByProductIdWithPagination(Long productId, int limit, Key exclusiveStartKey);

    List<ProductReview> findByUserId(Long userId);

    ProductReview loadReview(Long productId, Long reviewId);

    void deleteReview(ProductReview review);
}
