package com.bank;

import com.bank.jobs.CustomerEnrichmentJob;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineJobRunner implements CommandLineRunner {

    private final CustomerEnrichmentJob job;

    public CommandLineJobRunner(CustomerEnrichmentJob job) {
        this.job = job;
    }

    @Override
    public void run(String... args) {

        if (args.length == 0) return;

        if ("enrichCustomer".equals(args[0])) {
            int count = job.run();
            System.out.println("Enriched records: " + count);
        }
    }
}