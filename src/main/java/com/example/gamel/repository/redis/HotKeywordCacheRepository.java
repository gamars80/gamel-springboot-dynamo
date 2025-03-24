package com.example.gamel.repository.redis;

import com.example.gamel.dto.HotKeywordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HotKeywordCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String HOT_KEYWORDS_KEY = "hot_keywords";

    public void saveHotKeywords(List<HotKeywordDto> hotKeywords) {
        // 기존 데이터를 삭제
        redisTemplate.delete(HOT_KEYWORDS_KEY);
        // 각 핫 키워드를 Redis Sorted Set에 추가
        hotKeywords.forEach(dto ->
                redisTemplate.opsForZSet().add(HOT_KEYWORDS_KEY, dto.getKeyword(), dto.getCount())
        );
    }

    public List<HotKeywordDto> getHotKeywords() {
        Set<ZSetOperations.TypedTuple<Object>> resultSet = redisTemplate.opsForZSet()
                .reverseRangeWithScores(HOT_KEYWORDS_KEY, 0, 9);

        if (resultSet == null || resultSet.isEmpty()) {
            return List.of();
        }

        return resultSet.stream()
                .map(tuple -> new HotKeywordDto(
                        (String) tuple.getValue(),
                        Optional.ofNullable(tuple.getScore()).map(Double::intValue).orElse(0)
                ))
                .collect(Collectors.toList());
    }
}