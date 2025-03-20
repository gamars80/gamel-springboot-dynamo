package com.example.gamel.repository.redis;

import com.example.gamel.dto.HotKeywordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HotKeywordCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String HOT_KEYWORDS_KEY = "hot_keywords";

    public void saveHotKeywords(List<HotKeywordDto> keywords) {
        redisTemplate.opsForValue().set(HOT_KEYWORDS_KEY, keywords, Duration.ofMinutes(15));
    }

    @SuppressWarnings("unchecked")
    public List<HotKeywordDto> getHotKeywords() {
        Object result = redisTemplate.opsForValue().get(HOT_KEYWORDS_KEY);
        return result != null ? (List<HotKeywordDto>) result : List.of();
    }
}