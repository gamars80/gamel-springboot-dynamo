package com.example.gamel.contoller;


import com.example.gamel.dto.HotKeywordDto;
import com.example.gamel.repository.redis.HotKeywordCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/hot-keywords")
@RequiredArgsConstructor
public class HotKeywordController {

    private final HotKeywordCacheRepository hotKeywordCacheRepository;

    @GetMapping
    public ResponseEntity<List<HotKeywordDto>> getHotKeywords() {
        List<HotKeywordDto> hotKeywords = hotKeywordCacheRepository.getHotKeywords();
        return ResponseEntity.ok(hotKeywords);
    }
}