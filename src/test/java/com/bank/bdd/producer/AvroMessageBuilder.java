package com.bank.bdd.producer;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import java.io.InputStream;
import java.util.Map;

public class AvroMessageBuilder {
    private static final Schema CUSTOMER_SCHEMA = loadSchema();

    private static Schema loadSchema() {
        try (InputStream is =
                     AvroMessageBuilder.class
                             .getClassLoader()
                             .getResourceAsStream("avro/Customer.avsc")) {

            return new Schema.Parser().parse(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Customer.avsc");
        }
    }

    public static GenericRecord buildCustomer(Map<String, Object> data) {
        GenericRecord record = new GenericData.Record(CUSTOMER_SCHEMA);
        data.forEach(record::put);
        return record;
    }

    public static Schema getCustomerSchema() {
        return CUSTOMER_SCHEMA;
    }
}