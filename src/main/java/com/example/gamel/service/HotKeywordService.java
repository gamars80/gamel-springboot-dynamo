package com.example.gamel.service;

import com.example.gamel.dto.HotKeywordDto;
import com.example.gamel.repository.redis.HotKeywordCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotKeywordService {

    private final HotKeywordCacheRepository cacheRepository;

    public List<HotKeywordDto> getCachedHotKeywords() {
        return cacheRepository.getHotKeywords();
    }
}