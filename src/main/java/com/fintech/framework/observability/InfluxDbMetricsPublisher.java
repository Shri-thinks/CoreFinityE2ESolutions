package com.fintech.framework.observability;

import com.fintech.framework.config.ConfigFactory;
import com.fintech.framework.config.FrameworkConfig;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;

public final class InfluxDbMetricsPublisher {

    private static final Logger LOGGER = LogManager.getLogger(InfluxDbMetricsPublisher.class);
    private static volatile InfluxDBClient influxDBClient;

    private InfluxDbMetricsPublisher() {}

    private static InfluxDBClient getClient() {
        if (influxDBClient == null) {
            synchronized (InfluxDbMetricsPublisher.class) {
                if (influxDBClient == null) {
                    FrameworkConfig config = ConfigFactory.getConfig();
                    try {
                        influxDBClient = InfluxDBClientFactory.create(
                                config.influxDbUrl(),
                                config.influxDbToken().toCharArray(),
                                config.influxDbOrg(),
                                config.influxDbBucket()
                        );
                        LOGGER.info("InfluxDB client connected to {}", config.influxDbUrl());
                    } catch (Exception e) {
                        LOGGER.warn("Failed to connect to InfluxDB. Real-time telemetry disabled: {}", e.getMessage());
                    }
                }
            }
        }
        return influxDBClient;
    }

    public static void publishTestMetric(String testName, String testClass, String status, long durationMs, String env) {
        if (!ConfigFactory.getConfig().metricsEnabled()) {
            return;
        }

        try {
            InfluxDBClient client = getClient();
            if (client != null) {
                WriteApiBlocking writeApi = client.getWriteApiBlocking();
                Point point = Point.measurement("test_execution")
                        .addTag("test_name", testName)
                        .addTag("class", testClass)
                        .addTag("status", status)
                        .addTag("environment", env)
                        .addField("duration_ms", durationMs)
                        .time(Instant.now(), WritePrecision.MS);

                writeApi.writePoint(point);
                LOGGER.debug("Published metric to InfluxDB: {} status={} duration={}ms", testName, status, durationMs);
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to publish telemetry point to InfluxDB: {}", e.getMessage());
        }
    }

    public static void close() {
        if (influxDBClient != null) {
            influxDBClient.close();
            influxDBClient = null;
        }
    }
}
