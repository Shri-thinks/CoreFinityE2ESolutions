package com.fintech.framework.kafka;

import com.fintech.framework.config.ConfigFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.Future;

public final class KafkaProducerClient {

    private static final Logger LOGGER = LogManager.getLogger(KafkaProducerClient.class);
    private static KafkaProducer<String, String> producer;

    private KafkaProducerClient() {}

    private static synchronized KafkaProducer<String, String> getProducer() {
        if (producer == null) {
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ConfigFactory.getConfig().kafkaBootstrapServers());
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.ACKS_CONFIG, "all");
            props.put(ProducerConfig.RETRIES_CONFIG, 3);
            producer = new KafkaProducer<>(props);
            LOGGER.info("Kafka Producer initialized for bootstrap servers: {}", ConfigFactory.getConfig().kafkaBootstrapServers());
        }
        return producer;
    }

    public static Future<RecordMetadata> sendEvent(String topic, String key, String jsonPayload) {
        LOGGER.info("Publishing Kafka event to Topic: {} | Key: {} | Payload: {}", topic, key, jsonPayload);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, jsonPayload);
        return getProducer().send(record, (metadata, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to send Kafka message to topic {}", topic, exception);
            } else {
                LOGGER.info("Kafka message sent to partition {} at offset {}", metadata.partition(), metadata.offset());
            }
        });
    }

    public static synchronized void close() {
        if (producer != null) {
            producer.close();
            producer = null;
        }
    }
}
