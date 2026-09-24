package com.fintech.framework.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

public final class DatabaseUtils {

    private static final Logger LOGGER = LogManager.getLogger(DatabaseUtils.class);

    private DatabaseUtils() {}

    public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
        LOGGER.info("Executing DB Query: {}", sql);
        List<Map<String, Object>> resultList = new ArrayList<>();

        try (Connection connection = DatabaseConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> rowMap = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        rowMap.put(metaData.getColumnName(i).toLowerCase(), resultSet.getObject(i));
                    }
                    resultList.add(rowMap);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("SQL query execution failed: {}", sql, e);
            throw new RuntimeException("Database query failed: " + sql, e);
        }

        LOGGER.info("Query returned {} rows", resultList.size());
        return resultList;
    }

    public static Optional<Map<String, Object>> executeSingleRowQuery(String sql, Object... params) {
        List<Map<String, Object>> results = executeQuery(sql, params);
        if (results.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(results.get(0));
    }

    public static int executeUpdate(String sql, Object... params) {
        LOGGER.info("Executing DB Update: {}", sql);
        try (Connection connection = DatabaseConnectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("SQL update execution failed: {}", sql, e);
            throw new RuntimeException("Database update failed: " + sql, e);
        }
    }
}
