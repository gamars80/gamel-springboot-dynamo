package com.example.gamel.scheduler;

import com.example.gamel.service.HotKeywordReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotKeywordScheduler {

    private final HotKeywordReportService hotKeywordReportService;

    @Scheduled(cron = "0 */10 * * * *") // 10분마다 실행
    public void updateHotKeywords() {
        hotKeywordReportService.aggregateAndCacheHotKeywords();
    }
}