package com.bank.bdd.support;

import org.springframework.kafka.test.EmbeddedKafkaBroker;

public final class EmbeddedKafkaBrokerHolder {

    private static final EmbeddedKafkaBroker EMBEDDED_KAFKA =
            new EmbeddedKafkaBroker(1)
                    .kafkaPorts(0)
                    .brokerProperty("auto.create.topics.enable", "true")
                    .brokerProperty("listeners", "PLAINTEXT://127.0.0.1:0");

    static {
        EMBEDDED_KAFKA.afterPropertiesSet();
    }

    private EmbeddedKafkaBrokerHolder() {
    }

    public static String getBrokersAsString() {
        return EMBEDDED_KAFKA.getBrokersAsString();
    }

    public static void destroy() {
        EMBEDDED_KAFKA.destroy();
    }
}
