package com.example.gamel.repository.dynamo;

import com.example.gamel.entity.dynamo.SearchKeyword;
import com.example.gamel.repository.SearchKeywordRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DynamoSearchKeywordRepository implements SearchKeywordRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private DynamoDbTable<SearchKeyword> table;

    @PostConstruct
    public void init() {
        // "search_keywords" 테이블이 존재해야 하며, 스키마는 SearchKeyword에 맞춰 구성됨
        table = enhancedClient.table("search_keywords", TableSchema.fromBean(SearchKeyword.class));
    }

    @Override
    public Optional<SearchKeyword> findByKeywordAndTimeKey(String keyword, String timeKey) {
        Key key = Key.builder()
                .partitionValue(keyword)
                .sortValue(timeKey)
                .build();
        SearchKeyword item = table.getItem(r -> r.key(key));
        return Optional.ofNullable(item);
    }

    @Override
    public void save(SearchKeyword searchKeyword) {
        table.putItem(searchKeyword);
    }

    public List<SearchKeyword> findRecentSearchKeywords(int minutesAgo) {
        LocalDateTime thresholdTime = LocalDateTime.now().minusMinutes(minutesAgo);
        String timePrefix = thresholdTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"));

        // 전체 Scan 후 timeKey 기준 필터링 (정렬된 문자열 기준 비교)
        return table.scan()
                .items()
                .stream()
                .filter(item -> item.getTimeKey().compareTo(timePrefix) >= 0)
                .collect(Collectors.toList());
    }
}