package com.fintech.framework.database;

import com.fintech.framework.config.ConfigFactory;
import com.fintech.framework.config.FrameworkConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConnectionPool {

    private static final Logger LOGGER = LogManager.getLogger(DatabaseConnectionPool.class);
    private static volatile HikariDataSource dataSource;

    private DatabaseConnectionPool() {}

    private static HikariDataSource getDataSource() {
        if (dataSource == null) {
            synchronized (DatabaseConnectionPool.class) {
                if (dataSource == null) {
                    FrameworkConfig config = ConfigFactory.getConfig();

                    if (isPostgresReachable("localhost", 5432)) {
                        try {
                            HikariConfig hikariConfig = new HikariConfig();
                            hikariConfig.setJdbcUrl(config.dbUrl());
                            hikariConfig.setUsername(config.dbUsername());
                            hikariConfig.setPassword(config.dbPassword());
                            hikariConfig.setMaximumPoolSize(config.dbPoolMaxSize());
                            hikariConfig.setMinimumIdle(2);
                            hikariConfig.setConnectionTimeout(3000);
                            hikariConfig.setPoolName("Fintech-PostgresPool");

                            dataSource = new HikariDataSource(hikariConfig);
                            LOGGER.info("Connected to PostgreSQL DB at {}", config.dbUrl());
                            return dataSource;
                        } catch (Exception e) {
                            LOGGER.warn("Failed to connect to PostgreSQL. Initializing in-memory H2 fallback.", e);
                        }
                    } else {
                        LOGGER.info("PostgreSQL container not detected on localhost:5432. Initializing in-memory H2 database with schema.");
                    }

                    // Fallback to in-memory H2 DB (PostgreSQL mode)
                    HikariConfig h2Config = new HikariConfig();
                    h2Config.setJdbcUrl("jdbc:h2:mem:fintech_db;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
                    h2Config.setUsername("sa");
                    h2Config.setPassword("");
                    h2Config.setMaximumPoolSize(5);
                    h2Config.setPoolName("Fintech-H2FallbackPool");
                    dataSource = new HikariDataSource(h2Config);

                    initializeH2Schema();
                }
            }
        }
        return dataSource;
    }

    private static void initializeH2Schema() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            LOGGER.info("Creating in-memory schema for H2 database...");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    id VARCHAR(64) PRIMARY KEY,
                    first_name VARCHAR(100) NOT NULL,
                    last_name VARCHAR(100) NOT NULL,
                    email VARCHAR(150) UNIQUE NOT NULL,
                    kyc_status VARCHAR(30) DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS accounts (
                    id VARCHAR(64) PRIMARY KEY,
                    customer_id VARCHAR(64),
                    account_number VARCHAR(34) UNIQUE NOT NULL,
                    account_type VARCHAR(30) DEFAULT 'CHECKING',
                    available_balance NUMERIC(15, 2) DEFAULT 0.00,
                    hold_balance NUMERIC(15, 2) DEFAULT 0.00,
                    currency VARCHAR(3) DEFAULT 'USD',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS cards (
                    id VARCHAR(64) PRIMARY KEY,
                    account_id VARCHAR(64),
                    card_number_masked VARCHAR(20) NOT NULL,
                    card_token VARCHAR(128) UNIQUE NOT NULL,
                    card_type VARCHAR(20) DEFAULT 'VIRTUAL',
                    status VARCHAR(30) DEFAULT 'PENDING_ACTIVATION',
                    pin_hash VARCHAR(128),
                    daily_limit NUMERIC(10, 2) DEFAULT 1000.00,
                    expiry_date VARCHAR(7) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS authorizations (
                    id VARCHAR(64) PRIMARY KEY,
                    card_id VARCHAR(64),
                    auth_code VARCHAR(32) NOT NULL,
                    amount NUMERIC(10, 2) NOT NULL,
                    currency VARCHAR(3) DEFAULT 'USD',
                    merchant_name VARCHAR(150) NOT NULL,
                    status VARCHAR(30) NOT NULL,
                    response_code VARCHAR(10) DEFAULT '00',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                CREATE TABLE IF NOT EXISTS double_entry_ledger (
                    id VARCHAR(64) PRIMARY KEY,
                    transaction_id VARCHAR(64) NOT NULL,
                    account_id VARCHAR(64),
                    entry_type VARCHAR(10) NOT NULL,
                    amount NUMERIC(15, 2) NOT NULL,
                    balance_after NUMERIC(15, 2) NOT NULL,
                    description VARCHAR(255),
                    status VARCHAR(20) DEFAULT 'POSTED',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                MERGE INTO accounts KEY (id) VALUES ('ACC-DEMO-001', 'CUST-DEMO-001', 'ACCT9876543210', 'CHECKING', 5000.00, 0.00, 'USD', CURRENT_TIMESTAMP);
            """);

            LOGGER.info("In-memory H2 schema and seed data created successfully!");
        } catch (SQLException e) {
            LOGGER.error("Failed to initialize H2 in-memory schema", e);
        }
    }

    private static boolean isPostgresReachable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1000);
            return true;
        } catch (Exception e) {
            return false;
        }
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
