package com.bank.bdd.support;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Holder for the embedded Kafka broker instance created by Spring's @EmbeddedKafka
 * The actual broker is initialized through the CucumberSpringConfig
 */
public final class EmbeddedKafkaBrokerHolder {

    private static final AtomicReference<String> brokerString = new AtomicReference<>("localhost:9092");

    private EmbeddedKafkaBrokerHolder() {
    }

    public static void setBrokersAsString(String brokers) {
        brokerString.set(brokers);
    }

    public static String getBrokersAsString() {
        return brokerString.get();
    }

    public static void destroy() {
        // Destruction is handled by Spring's lifecycle management
    }
}
