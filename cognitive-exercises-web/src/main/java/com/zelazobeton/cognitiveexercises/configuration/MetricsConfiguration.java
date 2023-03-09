package com.zelazobeton.cognitiveexercises.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MetricsConfiguration {

    @Bean(name = "gameDataController.getGames")
    public Counter getGamesCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter("controller.gameDataController.getGames");
    }
}
