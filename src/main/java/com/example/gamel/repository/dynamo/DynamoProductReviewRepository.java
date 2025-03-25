package com.example.gamel.repository.dynamo;


import com.example.gamel.entity.dynamo.ProductReview;
import com.example.gamel.repository.ProductReviewRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class DynamoProductReviewRepository implements ProductReviewRepository {

    private final DynamoDbEnhancedClient enhancedClient;

    // 파생 필드(초기화가 필요함)는 @PostConstruct에서 설정
    private DynamoDbTable<ProductReview> productReviewTable;
    private DynamoDbIndex<ProductReview> productIdRatingIndex;
    private DynamoDbIndex<ProductReview> userIdIndex;

    @PostConstruct
    public void init() {
        this.productReviewTable = enhancedClient.table("product_reviews", TableSchema.fromBean(ProductReview.class));
        this.productIdRatingIndex = productReviewTable.index("ProductIdRatingIndex");
        this.userIdIndex = productReviewTable.index("UserIdIndex");
    }

    @Override
    public void save(ProductReview review) {
        productReviewTable.putItem(review);
    }

    @Override
    public List<ProductReview> findByProductId(Long productId) {
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

    @Override
    public Page<ProductReview> findByProductIdWithPagination(Long productId, int limit, Key exclusiveStartKey) {
        Key key = Key.builder().partitionValue(productId).build();
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(limit)
                .scanIndexForward(false);
        if (exclusiveStartKey != null) {
            requestBuilder.exclusiveStartKey(
                    exclusiveStartKey.keyMap(productReviewTable.tableSchema(), "")
            );
        }
        QueryEnhancedRequest request = requestBuilder.build();
        Iterator<Page<ProductReview>> pages = productIdRatingIndex.query(request).iterator();
        return pages.hasNext() ? pages.next() : null;
    }

    @Override
    public List<ProductReview> findByUserId(Long userId) {
        Key key = Key.builder().partitionValue(userId).build();
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

    @Override
    public ProductReview loadReview(Long productId, Long reviewId) {
        Key key = Key.builder()
                .partitionValue(String.valueOf(productId))
                .sortValue(String.valueOf(reviewId))
                .build();
        return productReviewTable.getItem(r -> r.key(key));
    }

    @Override
    public void deleteReview(ProductReview review) {
        productReviewTable.deleteItem(review);
    }
}
