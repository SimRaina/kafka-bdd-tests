package com.bank.jobs;

import com.bank.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerEnrichmentJob {

    private final JdbcTemplate jdbcTemplate;

    public CustomerEnrichmentJob(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int run() {

        List<Customer> customers =
                jdbcTemplate.query(
                        "SELECT CUSTOMER_ID, NAME FROM CUSTOMER",
                        (rs, rowNum) ->
                                new Customer(rs.getString("CUSTOMER_ID"),
                                        rs.getString("NAME"))
                );

        for (Customer c : customers) {
            String enrichedName = c.getName().toUpperCase();

            jdbcTemplate.update(
                    "INSERT INTO CUSTOMER_ENRICHED (customer_id, enriched_name) VALUES (?, ?)",
                    c.getId(),
                    enrichedName
            );
        }

        return customers.size();
    }
}
