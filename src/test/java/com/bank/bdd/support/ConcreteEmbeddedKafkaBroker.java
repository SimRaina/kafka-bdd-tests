package com.bank.bdd.support;

import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;

import java.util.Map;
import java.util.Set;
import java.util.Collections;

/**
 * Minimal Concrete implementation of EmbeddedKafkaBroker interface
 * The real broker initialization happens in EmbeddedKafkaBrokerHolder
 */
@SuppressWarnings({"NullableProblems", "all"})
public abstract class ConcreteEmbeddedKafkaBroker implements EmbeddedKafkaBroker {

    public ConcreteEmbeddedKafkaBroker() {
    }

    @Override
    public EmbeddedKafkaBroker kafkaPorts(int... ports) {
        return this;
    }

    @Override
    public Set<String> getTopics() {
        return Collections.emptySet();
    }

    @Override
    public EmbeddedKafkaBroker brokerProperties(Map<String, String> properties) {
        return this;
    }

    @Override
    public EmbeddedKafkaBroker brokerListProperty(String prefix) {
        return this;
    }

    @Override
    public EmbeddedKafkaBroker adminTimeout(int seconds) {
        return this;
    }

    @Override
    public void addTopics(String... topics) {
    }

    @Override
    public void addTopics(NewTopic... topics) {
    }

    @Override
    public Map addTopicsWithResults(String... topics) {
        return Collections.emptyMap();
    }

    @Override
    public Map addTopicsWithResults(NewTopic... topics) {
        return Collections.emptyMap();
    }

    @Override
    public String getBrokersAsString() {
        return "localhost:9092";
    }

    @Override
    public void consumeFromEmbeddedTopics(Consumer consumer, boolean seekToBeginning, String... topics) {
    }

    @Override
    public void consumeFromEmbeddedTopics(Consumer consumer, String... topics) {
    }

    @Override
    public void consumeFromAnEmbeddedTopic(Consumer consumer, boolean seekToBeginning, String topic) {
    }

    @Override
    public void consumeFromAnEmbeddedTopic(Consumer consumer, String topic) {
    }

    @Override
    public void consumeFromAllEmbeddedTopics(Consumer consumer, boolean seekToBeginning) {
    }

    @Override
    public void consumeFromAllEmbeddedTopics(Consumer consumer) {
    }

    @Override
    public void afterPropertiesSet() {
    }

    @Override
    public abstract int getPartitionsPerTopic();

    @Override
    public void destroy() {
    }
}
