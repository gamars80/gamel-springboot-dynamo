package com.example.gamel;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DynamoDbInitializer {

    private final DynamoDbClient dynamoDbClient;

    @PostConstruct
    public void createTablesIfNotExist() {
        List<String> tableNames = dynamoDbClient.listTables().tableNames();

        createSearchKeywordTable(tableNames);
        createProductReviewTable(tableNames);
        createRewardPointHistoryTable(tableNames);
    }

    private void createSearchKeywordTable(List<String> existingTables) {
        String tableName = "search_keywords";
        if (existingTables.contains(tableName)) return;

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(
                        KeySchemaElement.builder().attributeName("keyword").keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName("timeKey").keyType(KeyType.RANGE).build()
                )
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("keyword").attributeType(ScalarAttributeType.S).build(),
                        AttributeDefinition.builder().attributeName("timeKey").attributeType(ScalarAttributeType.S).build()
                )
                .provisionedThroughput(
                        ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build()
                )
                .build();

        dynamoDbClient.createTable(request);
        waitForTableToBecomeActive(tableName);
        System.out.println("✅ Created table: " + tableName);
    }

    private void createProductReviewTable(List<String> existingTables) {
        String tableName = "product_reviews";
        if (existingTables.contains(tableName)) return;

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(
                        KeySchemaElement.builder().attributeName("productId").keyType(KeyType.HASH).build(),
                        KeySchemaElement.builder().attributeName("reviewId").keyType(KeyType.RANGE).build()
                )
                .attributeDefinitions(
                        AttributeDefinition.builder().attributeName("productId").attributeType(ScalarAttributeType.N).build(),
                        AttributeDefinition.builder().attributeName("reviewId").attributeType(ScalarAttributeType.N).build(),
                        AttributeDefinition.builder().attributeName("userId").attributeType(ScalarAttributeType.N).build(),
                        AttributeDefinition.builder().attributeName("rating").attributeType(ScalarAttributeType.N).build()
                )
                .globalSecondaryIndexes(
                        GlobalSecondaryIndex.builder()
                                .indexName("UserIdIndex")
                                .keySchema(
                                        KeySchemaElement.builder().attributeName("userId").keyType(KeyType.HASH).build()
                                )
                                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                                .build(),
                        GlobalSecondaryIndex.builder()
                                .indexName("ProductIdRatingIndex")
                                .keySchema(
                                        KeySchemaElement.builder().attributeName("productId").keyType(KeyType.HASH).build(),
                                        KeySchemaElement.builder().attributeName("rating").keyType(KeyType.RANGE).build()
                                )
                                .projection(Projection.builder().projectionType(ProjectionType.ALL).build())
                                .provisionedThroughput(ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build())
                                .build()
                )
                .provisionedThroughput(
                        ProvisionedThroughput.builder().readCapacityUnits(5L).writeCapacityUnits(5L).build()
                )
                .build();

        dynamoDbClient.createTable(request);
        waitForTableToBecomeActive(tableName);
        System.out.println("✅ Created table: " + tableName);
    }

    private void createRewardPointHistoryTable(List<String> existingTables) {
        String tableName = "reward_point_history";
        if (existingTables.contains(tableName)) {
            System.out.println("Table already exists: " + tableName);
            return;
        }

        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(tableName)
                .keySchema(
                        // Partition Key: userId
                        KeySchemaElement.builder().attributeName("userId").keyType(KeyType.HASH).build(),
                        // Sort Key: reviewId
                        KeySchemaElement.builder().attributeName("reviewId").keyType(KeyType.RANGE).build()
                )
                .attributeDefinitions(
                        // userId: Number 타입
                        AttributeDefinition.builder().attributeName("userId").attributeType(ScalarAttributeType.N).build(),
                        // reviewId: Number 타입
                        AttributeDefinition.builder().attributeName("reviewId").attributeType(ScalarAttributeType.N).build()
                )
                .provisionedThroughput(
                        ProvisionedThroughput.builder()
                                .readCapacityUnits(5L)
                                .writeCapacityUnits(5L)
                                .build()
                )
                .build();

        dynamoDbClient.createTable(request);
        waitForTableToBecomeActive(tableName);
        System.out.println("✅ Created table: " + tableName);
    }

    private void waitForTableToBecomeActive(String tableName) {
        DescribeTableRequest describeTableRequest = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();
        long startTime = System.currentTimeMillis();
        long timeout = Duration.ofMinutes(2).toMillis();
        while (true) {
            DescribeTableResponse response = dynamoDbClient.describeTable(describeTableRequest);
            String tableStatus = response.table().tableStatusAsString();
            if ("ACTIVE".equalsIgnoreCase(tableStatus)) {
                break;
            }
            if (System.currentTimeMillis() - startTime > timeout) {
                throw new RuntimeException("Table " + tableName + " did not become ACTIVE within timeout.");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for table " + tableName + " to become active", e);
            }
        }
    }
}