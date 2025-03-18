package com.example.gamel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.gamel.repository")
@EntityScan(basePackages = "com.example.gamel.entity")
public class JpaConfig {
    // 추가적인 JPA 설정이 필요하면 이곳에 작성합니다.
}