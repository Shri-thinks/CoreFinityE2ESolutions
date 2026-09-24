package com.fintech.framework.kafka;

import com.fintech.framework.config.ConfigFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;

public final class KafkaProducerClient {

    private static final Logger LOGGER = LogManager.getLogger(KafkaProducerClient.class);
    private static KafkaProducer<String, String> producer;
    private static Boolean kafkaAvailable = null;

    // In-memory bus fallback when Kafka container is not running locally
    public static final ConcurrentHashMap<String, ConcurrentLinkedQueue<ProducerRecord<String, String>>> IN_MEMORY_TOPICS = new ConcurrentHashMap<>();

    private KafkaProducerClient() {}

    public static boolean isKafkaAvailable() {
        if (kafkaAvailable == null) {
            String servers = ConfigFactory.getConfig().kafkaBootstrapServers();
            String host = servers.contains(":") ? servers.split(":")[0] : "localhost";
            int port = servers.contains(":") ? Integer.parseInt(servers.split(":")[1]) : 9092;
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 800);
                kafkaAvailable = true;
                LOGGER.info("Kafka broker detected at {}:{}", host, port);
            } catch (Exception e) {
                kafkaAvailable = false;
                LOGGER.info("Kafka broker not reachable at {}:{}. Using high-speed In-Memory Event Bus fallback.", host, port);
            }
        }
        return kafkaAvailable;
    }

    private static synchronized KafkaProducer<String, String> getProducer() {
        if (producer == null && isKafkaAvailable()) {
            try {
                Properties props = new Properties();
                props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ConfigFactory.getConfig().kafkaBootstrapServers());
                props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
                props.put(ProducerConfig.ACKS_CONFIG, "all");
                props.put(ProducerConfig.RETRIES_CONFIG, 1);
                props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 2000);
                producer = new KafkaProducer<>(props);
            } catch (Exception e) {
                LOGGER.warn("Failed to create KafkaProducer instance, falling back to In-Memory Event Bus", e);
                kafkaAvailable = false;
            }
        }
        return producer;
    }

    public static Future<RecordMetadata> sendEvent(String topic, String key, String jsonPayload) {
        LOGGER.info("Publishing event to Topic: {} | Key: {} | Payload: {}", topic, key, jsonPayload);

        if (isKafkaAvailable() && getProducer() != null) {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, jsonPayload);
            return getProducer().send(record, (metadata, exception) -> {
                if (exception != null) {
                    LOGGER.error("Failed to send Kafka message", exception);
                } else {
                    LOGGER.info("Kafka message sent to partition {} at offset {}", metadata.partition(), metadata.offset());
                }
            });
        } else {
            // In-Memory Event Bus Fallback
            IN_MEMORY_TOPICS.computeIfAbsent(topic, k -> new ConcurrentLinkedQueue<>()).add(new ProducerRecord<>(topic, key, jsonPayload));
            LOGGER.info("Event successfully placed on in-memory topic queue: {}", topic);
            return CompletableFuture.completedFuture(null);
        }
    }

    public static synchronized void close() {
        if (producer != null) {
            producer.close();
            producer = null;
        }
    }
}
