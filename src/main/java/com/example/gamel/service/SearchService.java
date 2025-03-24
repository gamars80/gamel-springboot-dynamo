package com.example.gamel.service;

import com.example.gamel.entity.dynamo.SearchKeyword;
import com.example.gamel.repository.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final SearchKeywordRepository searchKeywordRepository;
    private final RedisTemplate<String, Object> redisTemplate; // RedisTemplate 주입

    private static final String HOT_KEYWORDS_KEY = "hot_keywords";

    public void recordSearch(String keyword) {
        String timeKey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));

        Optional<SearchKeyword> existing = searchKeywordRepository.findByKeywordAndTimeKey(keyword, timeKey);
        if (existing.isPresent()) {
            SearchKeyword searchKeyword = existing.get();
            searchKeyword.setCount(searchKeyword.getCount() + 1);
            searchKeywordRepository.save(searchKeyword);
        } else {
            SearchKeyword newKeyword = new SearchKeyword();
            newKeyword.setKeyword(keyword);
            newKeyword.setTimeKey(timeKey);
            newKeyword.setCount(1);
            searchKeywordRepository.save(newKeyword);
        }

        redisTemplate.opsForZSet().incrementScore(HOT_KEYWORDS_KEY, keyword, 1);
    }
}