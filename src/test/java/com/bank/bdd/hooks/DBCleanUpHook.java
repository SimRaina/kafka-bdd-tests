package com.bank.bdd.hooks;

import com.bank.bdd.support.MockApiServer;
import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class DBCleanUpHook {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MockApiServer mockApiServer;

    @Before("@setup")
    public void cleanDb() {
        mockApiServer.stop();

        System.out.println("*********Cleaning up BEFORE feature********");
        jdbcTemplate.execute("DROP TABLE IF EXISTS CUSTOMER");
        jdbcTemplate.execute("DROP TABLE IF EXISTS CUSTOMER_ENRICHED");
        jdbcTemplate.execute("DROP TABLE IF EXISTS CUSTOMER_API_RESPONSE");

        jdbcTemplate.execute("CREATE TABLE CUSTOMER (CUSTOMER_ID VARCHAR(20) PRIMARY KEY, NAME VARCHAR(100))");
        jdbcTemplate.execute("CREATE TABLE CUSTOMER_ENRICHED (CUSTOMER_ID VARCHAR(20) PRIMARY KEY, ENRICHED_NAME VARCHAR(100))");
        jdbcTemplate.execute("CREATE TABLE CUSTOMER_API_RESPONSE (CUSTOMER_ID VARCHAR(20) PRIMARY KEY, STATUS VARCHAR(50), SOURCE VARCHAR(50), RAW_RESPONSE CLOB)");

        System.out.println("*********Rows in Customer after cleanup********" +
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CUSTOMER",
                        Integer.class
                ));
    }
}
