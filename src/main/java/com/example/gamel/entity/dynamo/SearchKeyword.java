package com.example.gamel.entity.dynamo;

import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;

@Data
@DynamoDbBean
public class SearchKeyword {

    @Getter(onMethod_ = {@DynamoDbPartitionKey})
    private String keyword;

    @Getter(onMethod_ = {@DynamoDbSortKey})
    private String timeKey;

    @Getter(onMethod_ = {@DynamoDbAttribute("count")})
    private Integer count;
}