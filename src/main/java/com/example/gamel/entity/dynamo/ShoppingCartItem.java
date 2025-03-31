package com.example.gamel.entity.dynamo;

import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import lombok.*;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCartItem {

    private String userId;
    private String productId;
    private Integer quantity;
    private Long version; // 낙관적 잠금용

    @DynamoDbPartitionKey
    public String getUserId() {
        return userId;
    }

    @DynamoDbSortKey
    public String getProductId() {
        return productId;
    }

    @DynamoDbVersionAttribute
    public Long getVersion() {
        return version;
    }
}