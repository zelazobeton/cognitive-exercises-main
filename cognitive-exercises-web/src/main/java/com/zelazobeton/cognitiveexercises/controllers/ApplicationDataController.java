package com.zelazobeton.cognitiveexercises.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zelazobeton.cognitiveexercises.metrics.MetricsLoggingComponent;

@RestController
@RequestMapping(path = "/metrics/v1")
public class ApplicationDataController {
    private MetricsLoggingComponent metricsLoggingComponent;

    public ApplicationDataController(MetricsLoggingComponent metricsLoggingComponent) {
        this.metricsLoggingComponent = metricsLoggingComponent;
    }

    @GetMapping(produces = { "application/json" })
    public ResponseEntity<Object[][]> getGraphData() {
        return new ResponseEntity<>(this.metricsLoggingComponent.getGraphData(), HttpStatus.OK);
    }
}
