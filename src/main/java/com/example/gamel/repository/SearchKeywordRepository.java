package com.example.gamel.repository;

import com.example.gamel.entity.dynamo.SearchKeyword;

import java.util.List;
import java.util.Optional;

public interface SearchKeywordRepository {
    Optional<SearchKeyword> findByKeywordAndTimeKey(String keyword, String timeKey);
    void save(SearchKeyword searchKeyword);
    public List<SearchKeyword> findRecentSearchKeywords(int minutesAgo);
}