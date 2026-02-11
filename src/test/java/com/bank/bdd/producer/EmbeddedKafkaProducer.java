package com.bank.bdd.producer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
// import support.EmbeddedKafkaBrokerHolder;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class EmbeddedKafkaProducer {

    public static void send(String topic, String key, GenericRecord record, Schema schema) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                ByteArraySerializer.class);

        try (KafkaProducer<String, byte[]> producer =
                new KafkaProducer<>(props)) {

            byte[] avroBytes = serialize(record, schema);

            producer.send(
                    new ProducerRecord<>(topic, key, avroBytes)
            );
            producer.flush();
        }
    }

    private static byte[] serialize(
            GenericRecord record,
            Schema schema) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            DatumWriter<GenericRecord> writer =
                    new GenericDatumWriter<GenericRecord>(schema);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);

            writer.write(record, encoder);
            encoder.flush();
            return out.toByteArray();
        }catch(Exception e) {
           throw new RuntimeException();
        }
    }
}
