package com.example.gamel.service;


import com.example.gamel.dto.HotKeywordDto;
import com.example.gamel.entity.dynamo.SearchKeyword;
import com.example.gamel.repository.SearchKeywordRepository;
import com.example.gamel.repository.redis.HotKeywordCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HotKeywordReportService {

    private final SearchKeywordRepository searchKeywordRepository;
    private final HotKeywordCacheRepository cacheRepository;

    // DynamoDB에서 지난 1시간의 검색어 집계
    public void aggregateAndCacheHotKeywords() {
        List<SearchKeyword> recent = searchKeywordRepository.findRecentSearchKeywords(60);

        Map<String, Integer> aggregated = new HashMap<>();
        for (SearchKeyword keyword : recent) {
            aggregated.merge(keyword.getKeyword(), keyword.getCount(), Integer::sum);
        }

        List<HotKeywordDto> top10 = aggregated.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(e -> new HotKeywordDto(e.getKey(), e.getValue()))
                .toList();

        cacheRepository.saveHotKeywords(top10);
    }

    public List<HotKeywordDto> getHotKeywords() {
        return cacheRepository.getHotKeywords();
    }
}