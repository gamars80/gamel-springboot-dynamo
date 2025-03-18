package com.example.gamel.repository;


import com.amazonaws.services.dynamodbv2.datamodeling.QueryResultPage;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.example.gamel.entity.ProductReview;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

@Repository
public class ProductReviewRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public ProductReviewRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public void save(ProductReview review) {
        dynamoDBMapper.save(review);
    }

    public ProductReview findById(String reviewId) {
        return dynamoDBMapper.load(ProductReview.class, reviewId);
    }

    public List<ProductReview> findByProductId(Long productId) {
        DynamoDBQueryExpression<ProductReview> queryExpression = new DynamoDBQueryExpression<ProductReview>()
                .withIndexName("ProductIdRatingIndex")
                .withConsistentRead(false)
                .withKeyConditionExpression("productId = :v_productId")
                .withExpressionAttributeValues(Map.of(":v_productId", new AttributeValue().withN(String.valueOf(productId))))
                .withScanIndexForward(false);

        return dynamoDBMapper.query(ProductReview.class, queryExpression);
    }


    public QueryResultPage<ProductReview> findByProductIdWithPagination(Long productId, int limit, Map<String, AttributeValue> exclusiveStartKey) {
        DynamoDBQueryExpression<ProductReview> queryExpression = new DynamoDBQueryExpression<ProductReview>()
                .withIndexName("ProductIdRatingIndex")
                .withConsistentRead(false)
                .withKeyConditionExpression("productId = :v_productId")
                .withExpressionAttributeValues(Map.of(
                        ":v_productId", new AttributeValue().withN(String.valueOf(productId))
                ))
                .withScanIndexForward(false) // 내림차순 정렬 (높은 rating 우선)
                .withLimit(limit);           // 페이지당 항목 수 제한

        // 이전 페이지의 마지막 키가 있다면 설정
        if (exclusiveStartKey != null) {
            queryExpression.setExclusiveStartKey(exclusiveStartKey);
        }

        return dynamoDBMapper.queryPage(ProductReview.class, queryExpression);
    }

    public List<ProductReview> findByUserId(Long userId) {
        DynamoDBQueryExpression<ProductReview> queryExpression = new DynamoDBQueryExpression<ProductReview>()
                .withIndexName("UserIdIndex")
                .withConsistentRead(false)
                .withKeyConditionExpression("userId = :v_userId")
                .withExpressionAttributeValues(Map.of(":v_userId", new AttributeValue().withN(String.valueOf(userId))));

        return dynamoDBMapper.query(ProductReview.class, queryExpression);
    }

    public ProductReview loadReview(Long productId, Long reviewId) {
        return dynamoDBMapper.load(ProductReview.class, productId, reviewId);
    }

    public void deleteReview(ProductReview review) {
        dynamoDBMapper.delete(review);
    }
}