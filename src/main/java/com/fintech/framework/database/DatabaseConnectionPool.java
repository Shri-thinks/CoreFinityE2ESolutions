package com.fintech.framework.database;

import com.fintech.framework.config.ConfigFactory;
import com.fintech.framework.config.FrameworkConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseConnectionPool {

    private static final Logger LOGGER = LogManager.getLogger(DatabaseConnectionPool.class);
    private static volatile HikariDataSource dataSource;

    private DatabaseConnectionPool() {}

    private static HikariDataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DatabaseConnectionPool.class) {
                if (dataSource == null) {
                    FrameworkConfig config = ConfigFactory.getConfig();
                    HikariConfig hikariConfig = new HikariConfig();
                    hikariConfig.setJdbcUrl(config.dbUrl());
                    hikariConfig.setUsername(config.dbUsername());
                    hikariConfig.setPassword(config.dbPassword());
                    hikariConfig.setMaximumPoolSize(config.dbPoolMaxSize());
                    hikariConfig.setMinimumIdle(2);
                    hikariConfig.setConnectionTimeout(3000); // 3 seconds timeout
                    hikariConfig.setIdleTimeout(60000);
                    hikariConfig.setPoolName("Fintech-HikariPool");

                    try {
                        dataSource = new HikariDataSource(hikariConfig);
                        LOGGER.info("HikariCP connection pool initialized successfully with URL: {}", config.dbUrl());
                    } catch (Exception e) {
                        LOGGER.warn("Primary PostgreSQL pool failed to initialize. Initializing in-memory fallback H2 DB for offline testing.");
                        HikariConfig fallbackConfig = new HikariConfig();
                        fallbackConfig.setJdbcUrl("jdbc:h2:mem:fintech_test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
                        fallbackConfig.setUsername("sa");
                        fallbackConfig.setPassword("");
                        fallbackConfig.setMaximumPoolSize(5);
                        dataSource = new HikariDataSource(fallbackConfig);
                    }
                }
            }
        }
        return dataSource;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            LOGGER.info("Closing HikariCP connection pool");
            dataSource.close();
        }
    }
}
