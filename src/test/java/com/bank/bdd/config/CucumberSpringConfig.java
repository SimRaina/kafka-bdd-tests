package com.bank.bdd.config;

import com.bank.Application;
import com.bank.bdd.support.EmbeddedKafkaBrokerHolder;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = Application.class)
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "auto.create.topics.enable=true",
                "num.partitions=1"
        },
        topics = "customer-data"
)
@ActiveProfiles("test")
public class CucumberSpringConfig {

    @Autowired(required = false)
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    /**
     * Register the embedded Kafka broker address for use in test steps
     */
    public CucumberSpringConfig() {
        // Will be called after autowiring
    }

    @Autowired(required = false)
    public void setEmbeddedKafkaBroker(EmbeddedKafkaBroker broker) {
        if (broker != null) {
            String brokerAddress = broker.getBrokersAsString();
            System.out.println("Setting embedded Kafka broker: " + brokerAddress);
            EmbeddedKafkaBrokerHolder.setBrokersAsString(brokerAddress);
        }
    }
}
