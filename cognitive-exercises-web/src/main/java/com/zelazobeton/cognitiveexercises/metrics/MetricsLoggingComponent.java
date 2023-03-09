package com.zelazobeton.cognitiveexercises.metrics;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MetricsLoggingComponent {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Autowired
    private MeterRegistry meterRegistry;

    private final List<List<String>> metersByMinute;
    private final List<String> meters;

    public MetricsLoggingComponent() {
        this.metersByMinute = new ArrayList<>();
        this.meters = new ArrayList<>();
    }

    // inMemory PoC solution
    public Object[][] getGraphData() {
        final int colCount = this.meters.size() + 1;
        final int rowCount = this.metersByMinute.size() + 1;
        final Object[][] result = new Object[rowCount][colCount];

        final Date current = new Date();
        result[0][0] = "Time";
        int j = 1;
        for (final String meter : this.meters) {
            result[0][j] = meter;
            j++;
        }

        List<String> minuteOfStatuses;
        for (int i = 1; i < rowCount; i++) {
            minuteOfStatuses = this.metersByMinute.get(i - 1);
            result[i][0] = dateFormat.format(new Date(current.getTime() - (60000 * (rowCount - i))));
            for (j = 1; j <= minuteOfStatuses.size(); j++) {
                result[i][j] = minuteOfStatuses.get(j - 1);
            }
        }
        return result;
    }

    // Non - API

    @Scheduled(fixedDelay = 60000)
    private void exportMetrics() {
        final List<String> lastMinuteMetrics = new ArrayList<>();

        this.initializeStatuses(lastMinuteMetrics);
        this.updateMetrics(lastMinuteMetrics);

        this.metersByMinute.add(lastMinuteMetrics);
    }

    private void updateMetrics(final List<String> lastMinuteMetrics) {
        String meterId;
        int indexOfMeter;
        for (final Meter meter : this.meterRegistry.getMeters()) {
            meterId = meter.getId()
                    .toString();
            if (!this.meters.contains(meterId)) {
                this.meters.add(meterId);
                lastMinuteMetrics.add("");
            }
            StringBuilder meterMeasurements = new StringBuilder();
            for (final Measurement measurement : meter.measure()) {
                meterMeasurements.append(measurement.getStatistic()
                        .name() + " " + measurement.getValue());
            }
            indexOfMeter = this.meters.indexOf(meterId);
            lastMinuteMetrics.set(indexOfMeter, meterMeasurements.toString());
        }
    }

    private void initializeStatuses(final List<String> measurements) {
        for (int i = 0; i < this.meters.size(); i++) {
            measurements.add("");
        }
    }

    @Scheduled(fixedRate = 1000 * 30) // every 30 seconds
    public void exportMetricsByLogger() {
        this.meterRegistry.getMeters()
                .forEach(this::log);
    }

    private void log(Meter meter) {
        StringBuilder result = new StringBuilder();
        for (final Measurement measurement : meter.measure()) {
            result.append("[");
            result.append(measurement.getStatistic()
                    .name());
            result.append(" ");
            result.append(measurement.getValue());
            result.append("]");
        }
        log.trace("Reporting metric {}={}", meter.getId()
                .toString(), result.toString());
    }
}
