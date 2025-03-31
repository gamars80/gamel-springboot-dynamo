package com.example.gamel.service;


import com.example.gamel.annotaion.DistributedLock;
import com.example.gamel.entity.Product;
import com.example.gamel.entity.dynamo.ShoppingCartItem;
import com.example.gamel.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final ProductRepository productRepository;
    private static final int MAX_RETRIES = 3;

    private DynamoDbTable<ShoppingCartItem> getCartTable() {
        return dynamoDbEnhancedClient.table("ShoppingCart", TableSchema.fromBean(ShoppingCartItem.class));
    }

    private void updateItemWithRetry(DynamoDbTable<ShoppingCartItem> table, ShoppingCartItem item) {
        int attempts = 0;
        while (true) {
            try {
                table.updateItem(item);
                break;
            } catch (ConditionalCheckFailedException ex) {
                if (++attempts >= MAX_RETRIES) throw ex;
                Key key = Key.builder()
                        .partitionValue(item.getUserId())
                        .sortValue(item.getProductId())
                        .build();
                ShoppingCartItem latest = table.getItem(r -> r.key(key));
                if (latest != null) {
                    item.setQuantity(latest.getQuantity() + (item.getQuantity() - latest.getQuantity()));
                    item.setVersion(latest.getVersion());
                }
            }
        }
    }

    @DistributedLock(userId = "#userId")
    public void addItem(String userId, String productId, Integer quantity) {
        DynamoDbTable<ShoppingCartItem> table = getCartTable();
        Key key = Key.builder().partitionValue(userId).sortValue(productId).build();
        ShoppingCartItem item = table.getItem(r -> r.key(key));
        if (item != null) {
            item.setQuantity(item.getQuantity() + quantity);
            updateItemWithRetry(table, item);
        } else {
            table.putItem(ShoppingCartItem.builder()
                    .userId(userId)
                    .productId(productId)
                    .quantity(quantity)
                    .build());
        }
    }

    public List<CartItemResponse> getCartItems(String userId) {
        DynamoDbTable<ShoppingCartItem> table = getCartTable();
        List<ShoppingCartItem> items = table.query(r ->
                r.queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(userId).build()))
        ).items().stream().toList();
        return items.stream().map(item -> {
            Long prodId = Long.valueOf(item.getProductId());
            Product product = productRepository.findById(prodId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + prodId));
            return new CartItemResponse(
                    product.getName(),
                    product.getThumbnailUrl(),
                    product.getPrice(),
                    item.getQuantity());
        }).collect(Collectors.toList());
    }

    @DistributedLock(userId = "#userId")
    public void updateItem(String userId, String productId, Integer quantity) {
        DynamoDbTable<ShoppingCartItem> table = getCartTable();
        Key key = Key.builder().partitionValue(userId).sortValue(productId).build();
        ShoppingCartItem item = table.getItem(r -> r.key(key));
        if (item == null) throw new RuntimeException("Cart item not found");
        item.setQuantity(quantity);
        updateItemWithRetry(table, item);
    }

    @DistributedLock(userId = "#userId")
    public void deleteItem(String userId, String productId) {
        DynamoDbTable<ShoppingCartItem> table = getCartTable();
        Key key = Key.builder().partitionValue(userId).sortValue(productId).build();
        table.deleteItem(r -> r.key(key));
    }

    @lombok.Value
    public static class CartItemResponse {
        String productName;
        String thumbnailUrl;
        Integer price;
        Integer quantity;
    }
}