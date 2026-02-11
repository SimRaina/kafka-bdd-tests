package com.bank.bdd.stepdefs;

import com.bank.bdd.consumer.TestKafkaConsumer;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.apache.avro.Schema;
import com.bank.bdd.producer.AvroMessageBuilder;
import com.bank.bdd.producer.EmbeddedKafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
// import support.EmbeddedKafkaBrokerHolder;

import java.util.Map;
import java.util.UUID;

public class KafkaSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Given("a customer Avro message is produced")
    public void produceMessage() {
        // Generate unique customer ID for each scenario execution
        String customerId = "CUST_" + UUID.randomUUID().toString().substring(0, 8);
        scenarioContext.setCurrentCustomerId(customerId);

        Schema schema;
        var record = AvroMessageBuilder.buildCustomer(
                Map.of(
                        "customerId", customerId,
                        "name", "John"
                )
        );

        EmbeddedKafkaProducer.send(
                "customer-data",
                customerId,
                record,
                AvroMessageBuilder.getCustomerSchema()
        );
    }

    @And("the customer message is processed")
    public void consumeMessage() {
        // Use unique group ID to avoid offset conflicts between test runs
        String uniqueGroupId = "test-group-" + UUID.randomUUID().toString();

        TestKafkaConsumer consumer =
                new TestKafkaConsumer(
                        "localhost:9092",
                        AvroMessageBuilder.getCustomerSchema(),
                        uniqueGroupId
                );
        System.out.println("Kafka is running with consumer group: " + uniqueGroupId);
        consumer.pollAndPersist();
        consumer.close();
    }
}
