package com.example.gamel.repository.dynamo;

import com.example.gamel.entity.dynamo.RewardPointHistory;
import com.example.gamel.repository.RewardPointHistoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
@RequiredArgsConstructor
public class DynamoRewardPointHistoryRepository implements RewardPointHistoryRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<RewardPointHistory> table;

    @PostConstruct
    public void init() {
        this.table = enhancedClient.table("reward_point_history", TableSchema.fromBean(RewardPointHistory.class));
    }

    @Override
    public void save(RewardPointHistory rewardPointHistory) {
        table.putItem(rewardPointHistory);
    }
}
