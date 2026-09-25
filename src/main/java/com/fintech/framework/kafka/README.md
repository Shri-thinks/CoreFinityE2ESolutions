# Asynchronous Event-Driven Testing Module (Apache Kafka)

**Tech Stack:** Java 17, Apache Kafka Clients 3.7, Awaitility 4.2, Jackson Databind 2.17  
**Key Classes:** `KafkaProducerClient.java`, `KafkaConsumerClient.java`, `PaymentEventKafkaTest.java`, `PaymentEventMessageDto.java`

---

## 1. Purpose of the Module

In modern cloud-native banking and card issuing architectures, services are **decoupled and event-driven**. When a cardholder swipes their card at a merchant terminal, the Authorization Engine processes the authorization and immediately publishes an event (e.g., `PAYMENT_AUTHORIZED`) to an Apache Kafka topic. Downstream services (Real-Time Fraud Detection, Customer Push Notifications, Accounting Ledger) consume this event asynchronously.

The purpose of this module is to:
1. **Validate Asynchronous Event Streaming:** Verify that core business actions trigger the correct Kafka events with valid payloads, headers, and schemas within strict SLA deadlines.
2. **Eliminate Flaky Sleep Delays:** Replace brittle, non-deterministic `Thread.sleep()` calls with **Awaitility**, which polls topics dynamically with millisecond precision.
3. **Prevent Test Deadlocks & Consumer Collisions:** Manage dynamic consumer groups (`fintech-qa-consumer-group-<UUID>`) to ensure parallel test executions don't steal or acknowledge each other's messages.
4. **Provide High-Speed In-Memory Bus Fallback:** Include an automatic `ConcurrentLinkedQueue` in-memory fallback so developers can run event verification offline without requiring a local Kafka cluster.

---

## 2. Workflow & Internal Lifecycle

```mermaid
flowchart LR
    subgraph Test_Runner["Test Runner (PaymentEventKafkaTest)"]
        Test[Test Method]
        Producer[KafkaProducerClient]
        Consumer[KafkaConsumerClient]
        Await[Awaitility Poller]
    end

    subgraph Kafka_Broker["Kafka Cluster / In-Memory Bus"]
        Topic[("payment-events (Topic)")]
    end

    Test -->|1. Publish Event (key=txnId)| Producer
    Producer -->|2. send(ProducerRecord)| Topic
    Test -->|3. waitForEventByKey(txnId, timeout=5s)| Consumer
    Consumer -->|4. Poll every 200ms| Await
    Await -->|5. Read ConsumerRecords| Topic
    Topic -->>|6. Record matched by Key| Await
    Await -->>|7. Deserialized Event JSON| Consumer
    Consumer -->>|8. ConsumerRecord| Test
    Test -->|9. Assert Status == APPROVED| AssertJ[AssertJ Assertions]
```

---

## 3. Integration within the Framework

* **Integration with Configuration (`FrameworkConfig`):** Reads `kafka.bootstrap.servers = localhost:9092` and `kafka.payment.topic = payment-events`.
* **Integration with Docker (`docker-compose.yml`):** Connects to the local Confluent Kafka and Zookeeper containers.
* **Integration with Domain Models (`PaymentEventMessageDto`):** Deserializes JSON event strings into strongly-typed Java objects for assertion.
* **Integration with E2E Golden Scenario:** Validates that after an authorization API returns `200 OK`, a corresponding event appears on Kafka before the ledger posts the hold.

---

## 4. Senior SDET & QA Lead (6+ Years) Interview Q&A

### Q1: "Why is `Thread.sleep()` unacceptable for testing asynchronous Kafka event streams, and how does Awaitility solve it?"
> **Answer:**  
> "`Thread.sleep()` is an anti-pattern for two critical reasons:
> 1. **False Positives & Flakiness:** If network latency or broker rebalancing takes 501ms and you slept for 500ms, the test fails even though the system functioned correctly.
> 2. **Wasted Pipeline Runtime:** If the event arrives in 50ms but you slept for 5 seconds 'just to be safe', running 100 tests wastes over 8 minutes of idle CPU time.
> 
> In `KafkaConsumerClient.java`, we use **Awaitility**:
> ```java
> Awaitility.await()
>     .atMost(Duration.ofSeconds(5))
>     .pollInterval(Duration.ofMillis(200))
>     .until(() -> pollAndMatchEvent(topic, expectedKey));
> ```
> Awaitility checks the condition every 200ms and returns **the exact millisecond** the record arrives. If the message never arrives within the 5-second SLA, it fails with an explicit timeout exception."

### Q2: "How do you ensure test isolation when multiple parallel tests consume from the same Kafka topic?"
> **Answer:**  
> "If parallel test threads share the same static Kafka `group.id`, Kafka will distribute topic partitions across those consumers. One consumer thread might read a message intended for another test, causing random test failures.  
> We solve this using **Dynamic Unique Consumer Groups**:
> ```java
> String dynamicGroupId = config.kafkaConsumerGroup() + "-" + UUID.randomUUID();
> ```
> Each test execution spins up its own consumer with a unique group ID and `auto.offset.reset = earliest`. This ensures every test receives its own broadcast copy of all partition messages, preventing consumer group contention."

### Q3: "How do you test message idempotency and duplicate event handling in fintech?"
> **Answer:**  
> "Network retries and Kafka's 'at-least-once' delivery semantics frequently produce duplicate events in production. In our framework, we test consumer idempotency by:
> 1. Using `KafkaProducerClient` to deliberately publish the **exact same payment event twice** with identical `eventId` and `cardId`.
> 2. Querying the downstream database (`double_entry_ledger`) via `LedgerDao`.
> 3. Asserting that despite receiving two identical Kafka messages, the ledger **only debited the account once** and ignored or deduplicated the second message based on the unique event ID."

### Q4: "What is a Dead Letter Queue (DLQ), and how do you automate DLQ testing?"
> **Answer:**  
> "A Dead Letter Queue (DLQ) is a dedicated Kafka topic (e.g., `payment-events-dlq`) where unprocessable, malformed, or poisoned pill messages are routed after exhausting retry attempts.  
> We automate DLQ testing by:
> 1. Publishing an invalid payload (e.g. invalid JSON, missing mandatory `currency` field, or non-existent card token).
> 2. Asserting that the main payment consumer rejects the message.
> 3. Using `KafkaConsumerClient` to subscribe to the DLQ topic and asserting that the malformed message arrived there with expected error headers (`x-death-reason`, `x-original-topic`)."
