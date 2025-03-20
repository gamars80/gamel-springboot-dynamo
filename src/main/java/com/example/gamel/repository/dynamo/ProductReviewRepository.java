package com.example.gamel.repository.dynamo;


import com.example.gamel.entity.dynamo.ProductReview;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class ProductReviewRepository {

    private final DynamoDbTable<ProductReview> productReviewTable;
    private final DynamoDbIndex<ProductReview> productIdRatingIndex;
    private final DynamoDbIndex<ProductReview> userIdIndex;

    public ProductReviewRepository(DynamoDbEnhancedClient enhancedClient) {
        // 테이블 이름과 스키마는 실제 DynamoDB 설정에 맞게 변경하세요.
        this.productReviewTable = enhancedClient.table("ProductReviews", TableSchema.fromBean(ProductReview.class));
        this.productIdRatingIndex = productReviewTable.index("ProductIdRatingIndex");
        this.userIdIndex = productReviewTable.index("UserIdIndex");
    }


    // 저장
    public void save(ProductReview review) {
        productReviewTable.putItem(review);
    }

    public List<ProductReview> findByProductId(Long productId) {
        // 숫자 타입으로 직접 전달
        Key key = Key.builder()
                .partitionValue(productId)
                .build();
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();
        Iterator<Page<ProductReview>> pages = productIdRatingIndex.query(request).iterator();
        List<ProductReview> reviews = new ArrayList<>();
        while (pages.hasNext()) {
            Page<ProductReview> page = pages.next();
            reviews.addAll(page.items());
        }
        return reviews;
    }

    public Page<ProductReview> findByProductIdWithPagination(Long productId, int limit, Key exclusiveStartKey) {
        Key key = Key.builder()
                .partitionValue(productId)
                .build();
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit)
                .scanIndexForward(false); // 내림차순 정렬

        if (exclusiveStartKey != null) {
            requestBuilder.exclusiveStartKey(
                    exclusiveStartKey.keyMap(productReviewTable.tableSchema(), "")
            );
        }
        QueryEnhancedRequest request = requestBuilder.build();
        Iterator<Page<ProductReview>> pages = productIdRatingIndex.query(request).iterator();
        return pages.hasNext() ? pages.next() : null;
    }

    // userId로 조회 (인덱스 "UserIdIndex" 사용)
    public List<ProductReview> findByUserId(Long userId) {
        Key key = Key.builder()
                .partitionValue(userId)
                .build();
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .build();
        Iterator<Page<ProductReview>> pages = userIdIndex.query(request).iterator();
        List<ProductReview> reviews = new ArrayList<>();
        while (pages.hasNext()) {
            Page<ProductReview> page = pages.next();
            reviews.addAll(page.items());
        }
        return reviews;
    }

    // 복합키 조회: productId와 reviewId를 사용 (테이블의 기본키가 복합키라고 가정)
    public ProductReview loadReview(Long productId, Long reviewId) {
        Key key = Key.builder()
                .partitionValue(String.valueOf(productId))
                .sortValue(String.valueOf(reviewId))
                .build();
        return productReviewTable.getItem(r -> r.key(key));
    }

    // 삭제
    public void deleteReview(ProductReview review) {
        productReviewTable.deleteItem(review);
    }
}
