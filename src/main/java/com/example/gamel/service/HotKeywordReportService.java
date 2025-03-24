package com.example.gamel.service;


import com.example.gamel.dto.HotKeywordDto;
import com.example.gamel.entity.dynamo.SearchKeyword;
import com.example.gamel.repository.SearchKeywordRepository;
import com.example.gamel.repository.redis.HotKeywordCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class HotKeywordReportService {

    private final SearchKeywordRepository searchKeywordRepository;
    private final HotKeywordCacheRepository cacheRepository;

    /**
     * DynamoDB에서 지난 1시간 동안의 검색어 데이터를 집계하여 상위 10개의 핫 키워드를 Redis에 캐싱합니다.
     */
    public void aggregateAndCacheHotKeywords() {
        List<SearchKeyword> recentSearchKeywords = searchKeywordRepository.findRecentSearchKeywords(60);

        // 각 검색어별 count를 합산하여 집계합니다.
        Map<String, Integer> aggregatedCounts = recentSearchKeywords.stream()
                .collect(Collectors.toMap(
                        SearchKeyword::getKeyword,
                        SearchKeyword::getCount,
                        Integer::sum));

        // 집계된 결과에서 상위 10개의 핫 키워드를 추출합니다.
        List<HotKeywordDto> top10Keywords = aggregatedCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(10)
                .map(entry -> new HotKeywordDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        cacheRepository.saveHotKeywords(top10Keywords);
    }

    public List<HotKeywordDto> getHotKeywords() {
        return cacheRepository.getHotKeywords();
    }
}