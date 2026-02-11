package com.bank.bdd.consumer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import com.bank.bdd.support.H2Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * This consumer mimics the real AWS service logic:
 * Consume
 * Deserialize AVRO
 * Persist to DB
 */

public class TestKafkaConsumer {

    private final KafkaConsumer<String, byte[]> consumer;
    private final Schema schema;

    public TestKafkaConsumer(
            String bootstrapServers,
            Schema schema,
            String groupId
    ) {
        this.schema = schema;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ByteArrayDeserializer.class.getName());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        this.consumer = new KafkaConsumer<>(props);
        this.consumer.subscribe(Collections.singletonList("customer-data"));
        }

    public void pollAndPersist() {
        long end = System.currentTimeMillis() + 10_000;
        while(System.currentTimeMillis() < end) {
            ConsumerRecords<String, byte[]> records =
                    consumer.poll(Duration.ofMillis(500));
            if(!records.isEmpty()) {
                for(ConsumerRecord<String, byte[]> record : records) {
                    persist(record.value());
                }
                return;
            }
        }
        throw new RuntimeException("No Kafka records consumed");
    }

    private void persist(byte[] avroBytes) {
        try {
            BinaryDecoder decoder =
                    DecoderFactory.get().binaryDecoder(avroBytes, null);

            DatumReader<GenericRecord> reader =
                    new GenericDatumReader<>(schema);

            GenericRecord record = reader.read(null, decoder);

            try (Connection conn = H2Database.getConnection()) {
                PreparedStatement ps =
                        conn.prepareStatement(
                        "INSERT INTO CUSTOMER VALUES (?, ?)"
                );

                ps.setString(1, record.get("customerId").toString());
                ps.setString(2, record.get("name").toString());
                ps.execute();
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        if (consumer != null) {
            consumer.close();
        }
    }
}
