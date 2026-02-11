package com.bank.bdd.hooks;

import com.bank.bdd.support.EmbeddedKafkaBrokerHolder;
import io.cucumber.java.AfterAll;

public class EmbeddedKafkaHook {

    @AfterAll
    public static void stopEmbeddedKafka() {
        EmbeddedKafkaBrokerHolder.destroy();
    }
}
