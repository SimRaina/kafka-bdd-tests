package com.bank.bdd.support;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;

public class H2Database {

    private static final String JDBC_URL =
            "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

    static {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();

            stmt.execute("""
                    CREATE TABLE CUSTOMER (CUSTOMER_ID VARCHAR(20) PRIMARY KEY,
                    NAME VARCHAR(100)
                    )
                    """);
            conn.close();
        }catch(Exception e) {
            throw new RuntimeException("Failed to initialize H2", e);
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(JDBC_URL);
    }
}
