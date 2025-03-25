package com.example.gamel.entity.dynamo;

import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Data
@DynamoDbBean
public class RewardPointHistory {

    @Getter(onMethod_ = {@DynamoDbPartitionKey})
    private Long userId;

    @Getter(onMethod_ = {@DynamoDbSortKey})
    private Long reviewId;

    @Getter(onMethod_ = {@DynamoDbAttribute("productId")})
    private Long productId;

    @Getter(onMethod_ = {@DynamoDbAttribute("points")})
    private int points;
}