package com.example.gamel.repository;

import com.example.gamel.entity.dynamo.RewardPointHistory;

public interface RewardPointHistoryRepository {

    void save(RewardPointHistory rewardPointHistory);

}