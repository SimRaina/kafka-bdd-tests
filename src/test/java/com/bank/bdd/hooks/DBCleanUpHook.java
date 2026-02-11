package com.bank.bdd.hooks;

import io.cucumber.java.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

public class DBCleanUpHook {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Before("@setup")
    public void cleanDb() {
        System.out.println("*********Cleaning up BEFORE feature********");
        jdbcTemplate.execute("DROP TABLE IF EXISTS CUSTOMER");
        jdbcTemplate.execute("DROP TABLE IF EXISTS CUSTOMER_ENRICHED");

        jdbcTemplate.execute("CREATE TABLE CUSTOMER (CUSTOMER_ID VARCHAR(20) PRIMARY KEY, NAME VARCHAR(100))");
        jdbcTemplate.execute("CREATE TABLE CUSTOMER_ENRICHED (CUSTOMER_ID VARCHAR(20) PRIMARY KEY, ENRICHED_NAME VARCHAR(100))");

        System.out.println("*********Rows in Customer after cleanup********"+
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM CUSTOMER",
                        Integer.class
                ));
    }
}
