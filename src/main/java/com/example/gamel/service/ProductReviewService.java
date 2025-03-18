package com.example.gamel.service;

import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.gamel.dto.PaginatedProductReview;
import com.example.gamel.entity.ProductReview;
import com.example.gamel.exceptions.ResourceNotFoundException;
import com.example.gamel.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductReviewService {
    private final ProductReviewRepository productReviewRepository;

    public void addReview(ProductReview review){
        productReviewRepository.save(review);
    }

    public List<ProductReview> getReviewsByProductId(Long productId){
        return productReviewRepository.findByProductId(productId);
    }

    public PaginatedProductReview getPaginatedReviews(Long productId, int limit, Map<String, AttributeValue> exclusiveStartKey) {
        QueryResultPage<ProductReview> resultPage = productReviewRepository.findByProductIdWithPagination(productId, limit, exclusiveStartKey);

        PaginatedProductReview paginatedResult = new PaginatedProductReview();
        paginatedResult.setReviews(resultPage.getResults());
        // 변환 메서드를 통해 단순화된 LastEvaluatedKey 설정
        paginatedResult.setLastEvaluatedKey(simplifyLastEvaluatedKey(resultPage.getLastEvaluatedKey()));

        return paginatedResult;
    }


    public List<ProductReview> getReviewsByUserId(Long userId){
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
                // 숫자형(n)이 있으면 그 값을, 그렇지 않으면 문자열(s)을 사용
                if (value.getN() != null) {
                    simpleMap.put(entry.getKey(), value.getN());
                } else if (value.getS() != null) {
                    simpleMap.put(entry.getKey(), value.getS());
                }
            }
        }
        return simpleMap;
    }

}
