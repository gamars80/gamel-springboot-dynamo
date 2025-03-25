package com.example.gamel.service;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.gamel.dto.PaginatedProductReview;
import com.example.gamel.entity.Point;
import com.example.gamel.entity.dynamo.ProductReview;
import com.example.gamel.entity.dynamo.RewardPointHistory;
import com.example.gamel.exceptions.ResourceNotFoundException;
import com.example.gamel.repository.PointRepository;
import com.example.gamel.repository.RewardPointHistoryRepository;
import com.example.gamel.repository.dynamo.DynamoProductReviewRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final DynamoProductReviewRepository productReviewRepository;

    private final DynamoProductReviewRepository reviewRepository;
    private final PointRepository pointRepository; // MySQL 적립금 저장소
    private final RewardPointHistoryRepository rewardPointHistoryRepository; // DynamoDB 보상 내역 저장소


    public void addReview(ProductReview review) {
        // 1. 리뷰 저장
        reviewRepository.save(review);

        // 2. 보상 내역 생성 (예: 고정 10점 지급)
        Point reward = Point.create(review.getUserId(), review.getProductId(), review.getReviewId(), 10);

        Point savedReward = null;
        try {
            // 3. MySQL에 보상 적립금 저장
            savedReward = pointRepository.save(reward);

            // 4. DynamoDB에 보상 내역 저장
            RewardPointHistory rewardPointHistory = new RewardPointHistory();
            rewardPointHistory.setUserId(savedReward.getUserId());
            rewardPointHistory.setReviewId(savedReward.getReviewId());
            rewardPointHistory.setProductId(savedReward.getProductId());
            rewardPointHistory.setPoints(savedReward.getPoints());
            rewardPointHistoryRepository.save(rewardPointHistory);
        } catch (Exception e) {
            // 어느 단계라도 실패하면 보상(롤백) 처리
            rollback(review, savedReward);
            throw new RuntimeException("리뷰 등록 및 보상 처리 실패: " + e.getMessage(), e);
        }
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

    private void rollback(ProductReview review, Point savedReward) {
        // 보상 내역(MySQL)이 저장되었다면 삭제 시도
        if (savedReward != null) {
            try {
                pointRepository.deleteById(savedReward.getId());
            } catch (Exception ex) {
                System.err.println("MySQL 보상 롤백 실패: " + ex.getMessage());
            }
        }
        // 리뷰 등록된 내용 삭제 시도
        try {
            reviewRepository.deleteReview(review);
        } catch (Exception ex) {
            System.err.println("리뷰 롤백 실패: " + ex.getMessage());
        }
    }
}

