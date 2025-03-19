package com.example.gamel.entity.dynamo;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import lombok.Data;
import lombok.Getter;


@Data
@DynamoDbBean
public class ProductReview {

    // 메인 테이블 파티션 키 및 ProductIdRatingIndex의 파티션 키로 사용
    @Getter(onMethod_ = {
            @DynamoDbPartitionKey,
            @DynamoDbSecondaryPartitionKey(indexNames = "ProductIdRatingIndex")
    })
    private Long productId;

    // 메인 테이블의 정렬 키
    @Getter(onMethod_ = {@DynamoDbSortKey})
    private Long reviewId;

    // UserIdIndex의 파티션 키로 사용
    @Getter(onMethod_ = {
            @DynamoDbAttribute("userId"),
            @DynamoDbSecondaryPartitionKey(indexNames = "UserIdIndex")
    })
    private Long userId;

    // ProductIdRatingIndex의 정렬 키로 사용
    @Getter(onMethod_ = {
            @DynamoDbAttribute("rating"),
            @DynamoDbSecondarySortKey(indexNames = "ProductIdRatingIndex")
    })
    private Long rating;

    @Getter(onMethod_ = {@DynamoDbAttribute("reviewText")})
    private String reviewText;

    @Getter(onMethod_ = {@DynamoDbAttribute("content")})
    private String content;

    @Getter(onMethod_ = {@DynamoDbAttribute("createdAt")})
    private String createdAt;
}
