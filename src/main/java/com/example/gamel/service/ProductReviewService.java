package com.example.gamel.service;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.gamel.dto.PaginatedProductReview;
import com.example.gamel.entity.dynamo.ProductReview;
import com.example.gamel.exceptions.ResourceNotFoundException;
import com.example.gamel.repository.dynamo.ProductReviewRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;

    public void addReview(ProductReview review) {
        productReviewRepository.save(review);
    }

    public List<ProductReview> getReviewsByProductId(Long productId) {
        return productReviewRepository.findByProductId(productId);
    }

    public PaginatedProductReview getPaginatedReviews(Long productId, int limit, Map<String, AttributeValue> exclusiveStartKeyMap) {
        Key exclusiveStartKey = null;
        if (exclusiveStartKeyMap != null && !exclusiveStartKeyMap.isEmpty()) {
            // 예시: 파티션키는 "productId", 정렬키는 "reviewId"로 가정 (테이블 설계에 따라 변경)
            String partitionVal = exclusiveStartKeyMap.get("productId").s();
            String sortVal = exclusiveStartKeyMap.get("reviewId").s();
            exclusiveStartKey = Key.builder()
                    .partitionValue(partitionVal)
                    .sortValue(sortVal)
                    .build();
        }

        Page<ProductReview> resultPage = productReviewRepository.findByProductIdWithPagination(productId, limit, exclusiveStartKey);

        PaginatedProductReview paginatedResult = new PaginatedProductReview();
        paginatedResult.setReviews(resultPage != null ? resultPage.items() : Collections.emptyList());
        paginatedResult.setLastEvaluatedKey(simplifyLastEvaluatedKey(resultPage != null ? resultPage.lastEvaluatedKey() : null));

        return paginatedResult;
    }

    public List<ProductReview> getReviewsByUserId(Long userId) {
        return productReviewRepository.findByUserId(userId);
    }

    public ProductReview getReview(Long productId, Long reviewId) {
        return Optional.ofNullable(productReviewRepository.loadReview(productId, reviewId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product review not found for productId: " + productId + " and reviewId: " + reviewId));
    }

    public void deleteReview(Long productId, Long reviewId) {
        ProductReview review = Optional.ofNullable(productReviewRepository.loadReview(productId, reviewId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review not found for productId: " + productId + " and reviewId: " + reviewId));
        productReviewRepository.deleteReview(review);
    }

    public Map<String, String> simplifyLastEvaluatedKey(Map<String, AttributeValue> lastEvaluatedKey) {
        Map<String, String> simpleMap = new HashMap<>();
        if (lastEvaluatedKey != null) {
            for (Map.Entry<String, AttributeValue> entry : lastEvaluatedKey.entrySet()) {
                AttributeValue value = entry.getValue();
                if (value.n() != null && !value.n().isEmpty()) {
                    simpleMap.put(entry.getKey(), value.n());
                } else if (value.s() != null && !value.s().isEmpty()) {
                    simpleMap.put(entry.getKey(), value.s());
                }
            }
        }
        return simpleMap;
    }
}

