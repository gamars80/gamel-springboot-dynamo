package com.example.gamel.service;



import com.example.gamel.entity.Point;
import com.example.gamel.entity.dynamo.RewardPointHistory;
import com.example.gamel.repository.RewardPointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RewardPointHistoryService {

    private final RewardPointHistoryRepository rewardPointHistoryRepository;

    public void saveRewardPointHistory(Point point) {
        RewardPointHistory rewardPointHistory = new RewardPointHistory();
        rewardPointHistory.setUserId(point.getUserId());
        rewardPointHistory.setReviewId(point.getReviewId());
        rewardPointHistory.setProductId(point.getProductId());
        rewardPointHistory.setPoints(point.getPoints());

        // Repository를 통해 DynamoDB에 저장
        rewardPointHistoryRepository.save(rewardPointHistory);
    }
}