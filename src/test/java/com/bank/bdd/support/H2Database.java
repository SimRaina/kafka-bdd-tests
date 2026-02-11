package com.bank.bdd.support;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;

public class H2Database {

    private static final String JDBC_URL =
            "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";
    private static volatile boolean initialized = false;
    private static final Object lock = new Object();

    public static Connection getConnection() throws Exception {
        Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

        // Initialize table on first connection only (thread-safe lazy initialization)
        if (!initialized) {
            synchronized (lock) {
                if (!initialized) {
                    try {
                        Statement stmt = conn.createStatement();
                        stmt.execute("""
                                CREATE TABLE IF NOT EXISTS CUSTOMER (CUSTOMER_ID VARCHAR(20) PRIMARY KEY,
                                NAME VARCHAR(100)
                                )
                                """);
                        stmt.close();
                        initialized = true;
                    } catch (Exception e) {
                        // Table might already exist or other issue - log but don't fail
                        System.err.println("Note: Could not create CUSTOMER table: " + e.getMessage());
                        initialized = true;  // Mark as initialized even on error to avoid infinite retries
                    }
                }
            }
        }

        return conn;
    }
}
