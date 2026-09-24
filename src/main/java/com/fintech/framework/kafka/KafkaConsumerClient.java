package com.fintech.framework.kafka;

import com.fintech.framework.config.ConfigFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.awaitility.Awaitility;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public final class KafkaConsumerClient {

    private static final Logger LOGGER = LogManager.getLogger(KafkaConsumerClient.class);

    private KafkaConsumerClient() {}

    public static Properties getConsumerProperties(String groupId) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ConfigFactory.getConfig().kafkaBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        return props;
    }

    /**
     * Polls Kafka topic using Awaitility until a record with matching key arrives or timeout expires.
     */
    public static Optional<ConsumerRecord<String, String>> waitForEventByKey(String topic, String expectedKey, Duration timeout) {
        LOGGER.info("Awaiting Kafka event on topic '{}' with key '{}' (Timeout: {}s)", topic, expectedKey, timeout.toSeconds());
        String dynamicGroupId = ConfigFactory.getConfig().kafkaConsumerGroup() + "-" + UUID.randomUUID();
        AtomicReference<ConsumerRecord<String, String>> matchedRecord = new AtomicReference<>(null);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getConsumerProperties(dynamicGroupId))) {
            consumer.subscribe(Collections.singletonList(topic));

            try {
                Awaitility.await()
                        .atMost(timeout)
                        .pollInterval(Duration.ofMillis(300))
                        .until(() -> {
                            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(200));
                            for (ConsumerRecord<String, String> record : records) {
                                if (expectedKey.equals(record.key())) {
                                    LOGGER.info("Matched Kafka Record: Key={}, Value={}", record.key(), record.value());
                                    matchedRecord.set(record);
                                    return true;
                                }
                            }
                            return false;
                        });
            } catch (Exception e) {
                LOGGER.warn("Timeout reached waiting for Kafka event with key: {}", expectedKey);
            }
        } catch (Exception e) {
            LOGGER.error("Error creating Kafka consumer for topic: {}", topic, e);
        }

        return Optional.ofNullable(matchedRecord.get());
    }
}
