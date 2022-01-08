package com.zelazobeton.cognitiveexercises.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {
    public CacheConfiguration() {
    }

    @Bean
    public CacheManager tileImagesCacheManager() {
        return new ConcurrentMapCacheManager("tileImages");
    }
}