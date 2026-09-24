package com.fintech.framework.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
    "system:properties",
    "system:env",
    "classpath:config.properties"
})
public interface FrameworkConfig extends Config {

    @Key("env")
    @DefaultValue("qa")
    String environment();

    @Key("browser")
    @DefaultValue("chrome")
    String browser();

    @Key("headless")
    @DefaultValue("false")
    boolean headless();

    @Key("execution.mode")
    @DefaultValue("local") // local or grid
    String executionMode();

    @Key("selenium.grid.url")
    @DefaultValue("http://localhost:4444")
    String seleniumGridUrl();

    @Key("timeout.explicit.seconds")
    @DefaultValue("10")
    int explicitTimeoutSeconds();

    // API Configs
    @Key("base.api.url")
    @DefaultValue("http://localhost:8089") // WireMock default port
    String baseApiUrl();

    @Key("auth.token")
    @DefaultValue("Bearer test-fintech-jwt-token-xyz")
    String authToken();

    // UI Configs (Demo Target: ParaBank / Customer Portal)
    @Key("base.ui.url")
    @DefaultValue("https://parabank.parasoft.com/parabank")
    String baseUiUrl();

    // Database Configs (HikariCP / PostgreSQL)
    @Key("db.url")
    @DefaultValue("jdbc:postgresql://localhost:5432/fintech_db")
    String dbUrl();

    @Key("db.username")
    @DefaultValue("fintech_user")
    String dbUsername();

    @Key("db.password")
    @DefaultValue("fintech_pass")
    String dbPassword();

    @Key("db.pool.max.size")
    @DefaultValue("10")
    int dbPoolMaxSize();

    // Kafka Configs
    @Key("kafka.bootstrap.servers")
    @DefaultValue("localhost:9092")
    String kafkaBootstrapServers();

    @Key("kafka.payment.topic")
    @DefaultValue("payment-events")
    String kafkaPaymentTopic();

    @Key("kafka.consumer.group")
    @DefaultValue("fintech-qa-consumer-group")
    String kafkaConsumerGroup();

    // Observability (InfluxDB & Grafana)
    @Key("metrics.enabled")
    @DefaultValue("true")
    boolean metricsEnabled();

    @Key("influxdb.url")
    @DefaultValue("http://localhost:8086")
    String influxDbUrl();

    @Key("influxdb.token")
    @DefaultValue("fintech-super-secret-token")
    String influxDbToken();

    @Key("influxdb.org")
    @DefaultValue("fintech-qa")
    String influxDbOrg();

    @Key("influxdb.bucket")
    @DefaultValue("test-metrics")
    String influxDbBucket();
}
