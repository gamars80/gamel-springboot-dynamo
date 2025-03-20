package com.example.gamel.contoller;


import com.example.gamel.dto.HotKeywordDto;
import com.example.gamel.repository.redis.HotKeywordCacheRepository;
import com.example.gamel.service.HotKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Redis 캐시에 저장된 인기 검색어를 조회하는 REST API Controller
 */
@RestController
@RequestMapping("/api/hot-keywords")
@RequiredArgsConstructor
public class HotKeywordController {

    private final HotKeywordCacheRepository hotKeywordCacheRepository;

    /**
     * GET /api/hot-keywords
     * 캐시에 저장된 최신 인기 검색어 목록을 반환합니다.
     *
     * @return 인기 검색어 리스트와 HTTP 상태 코드 200(OK)
     */
    @GetMapping
    public ResponseEntity<List<HotKeywordDto>> getHotKeywords() {
        List<HotKeywordDto> hotKeywords = hotKeywordCacheRepository.getHotKeywords();
        return ResponseEntity.ok(hotKeywords);
    }
}