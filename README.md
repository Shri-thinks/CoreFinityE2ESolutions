# Fintech Enterprise Hybrid Test Automation Framework

![Java](https://img.shields.io/badge/Java-17-orange)
![Selenium](https://img.shields.io/badge/Selenium-4.19-green)
![TestNG](https://img.shields.io/badge/TestNG-7.9-blue)
![RestAssured](https://img.shields.io/badge/RestAssured-5.4-red)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.7-black)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![Kubernetes](https://img.shields.io/badge/Kubernetes-Job-326CE5)
![Grafana](https://img.shields.io/badge/Grafana-Live%20Telemetry-F46800)

Enterprise-grade Test Automation Framework designed for **SDET, QA Lead, and Senior Automation Engineer** roles. Specifically tailored for **Fintech, Card Issuing, Card Management, Authorization, and Double-Entry Ledger** domains.

---

## 🏛️ 1. Architecture Highlights

* **Thread-Safe UI Automation:** Fluent Page Object Model (POM) using `ThreadLocal<WebDriver>` within `DriverManager` to ensure **100% thread safety** during high-concurrency parallel runs (`parallel="methods"`).
* **Robust API Automation:** Built on `RestAssured` with a centralized `SpecFactory` and the **Lombok Builder pattern** for generating complex, dynamic financial payloads.
* **Asynchronous Event-Driven Testing (Kafka):** Validates asynchronous payment events emitted to Apache Kafka topics using **Awaitility** to prevent flaky hardcoded sleeps.
* **Relational Database Ledger Validations:** Uses a high-performance **HikariCP connection pool** with DAO patterns to query balances, hold amounts, and audit double-entry accounting ledgers.
* **Real-Time Observability (Grafana + InfluxDB):** Custom `GrafanaTestListener` intercepts TestNG execution and streams live test pass/fail telemetry to InfluxDB for real-time executive dashboarding.
* **Containerized Infrastructure & Kubernetes:** Includes `docker-compose.yml` for complete local testbeds and Kubernetes manifests (`k8s/test-runner-job.yaml`) for cloud-native distributed CI runs.
* **Multi-Stage CI/CD:** GitHub Actions pipeline (`.github/workflows/ci-cd-pipeline.yml`) with automated Docker builds, parallel headless test execution, and Allure report publishing.

---

## 📁 2. Project Directory Structure

```text
fintech-hybrid-framework/
├── pom.xml                                   # Core Maven build configuration
├── Dockerfile                                # Multi-stage container runner
├── docker-compose.yml                        # Kafka, Postgres, InfluxDB, Grafana, Selenium Grid, WireMock
├── .github/workflows/ci-cd-pipeline.yml      # CI/CD multi-stage GitHub Actions workflow
├── k8s/                                      # Kubernetes deployment manifests
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   └── test-runner-job.yaml
├── infra/
│   ├── init-db.sql                           # Core banking SQL schema & seed data
│   ├── grafana/provisioning/                 # Grafana auto-provisioned datasources & dashboards
│   └── wiremock/mappings/                    # Mock REST API responses
├── src/main/java/com/fintech/framework/
│   ├── config/                               # Owner library type-safe configs
│   ├── driver/                               # ThreadLocal WebDriver, Factory, and Options
│   ├── ui/                                   # Fluent BasePage with explicit wait wrappers
│   ├── api/                                  # SpecFactory and generic RestClient
│   ├── database/                             # HikariCP pool and DatabaseUtils
│   ├── kafka/                                # KafkaProducerClient and Awaitility Consumer
│   └── observability/                        # InfluxDbPublisher and GrafanaTestListener
└── src/test/java/com/fintech/
    ├── domain/                               # Business DAOs, DTO Models, and Page Objects
    └── tests/
        ├── ui/                               # Card Management Portal UI tests
        ├── api/                              # Customer KYC and Card Issuing API tests
        ├── kafka/                            # Asynchronous Payment Event streaming tests
        └── e2e/                              # Golden Scenario: UI + API + Kafka + DB
```

---

## 🚀 3. Quick Start (Running Locally)

### Prerequisites
* Java JDK 17+
* Maven 3.8+
* Docker & Docker Compose (Optional, for running full containerized testbed)

### Option A: Run Tests in Local Mode
```bash
mvn clean test
```
* Runs the test suite in Chrome against local/demo endpoints.
* Database queries will automatically use the high-speed in-memory H2 fallback if PostgreSQL is not running.

### Option B: Run Full Infrastructure via Docker Compose
To spin up **PostgreSQL, Apache Kafka, InfluxDB, Grafana, Selenium Grid, and WireMock**:
```bash
# 1. Start all infrastructure services
docker-compose up -d

# 2. Run the test suite against the containerized testbed
mvn clean test -Dexecution.mode=grid -Dselenium.grid.url=http://localhost:4444

# 3. View Live Grafana Dashboard
# Open http://localhost:3000 (User: admin / Pass: admin)
# Navigate to "Dashboards" -> "Fintech Automation Real-Time Telemetry"
```

---

## ☸️ 4. Running on Kubernetes (K8s)

Deploy the test suite as a Kubernetes `Job` in an isolated testing namespace:

```bash
# 1. Create QA Namespace
kubectl apply -f k8s/namespace.yaml

# 2. Apply ConfigMap and Secrets
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml

# 3. Trigger the Test Execution Job
kubectl apply -f k8s/test-runner-job.yaml

# 4. Stream Test Logs from K8s Pod
kubectl logs -f job/fintech-automation-suite -n fintech-qa
```

---

## 🎯 5. The Golden E2E Scenario (Interview Talking Point)

Located in: `src/test/java/com/fintech/tests/e2e/EndToEndFintechLifecycleTest.java`

1. **REST API:** Issue a Virtual Visa Card (`POST /api/v1/cards/issue`) with status `PENDING_ACTIVATION`.
2. **Database:** Verify through `CardDao` that the card record is inserted and status is `PENDING_ACTIVATION`.
3. **Selenium UI POM:** Launch browser, navigate to Card Management Portal, click "Activate Card", and set a secure 4-digit PIN.
4. **Database:** Re-query database to verify card state updated to `ACTIVE` and PIN hash is stored.
5. **Auth API:** Simulate a $150 Merchant Swipe authorization (`POST /api/v1/payments/authorize`).
6. **Kafka Stream:** Assert that a `PAYMENT_AUTHORIZED` event is published to topic `payment-events` using **Awaitility**.
7. **Database Ledger:** Assert that a pending authorization hold of $150 was debited in the double-entry accounting ledger.

---

## 🎤 6. Interview Defense Cheat Sheet (QA Lead & Senior SDET)

| Interviewer Question | Recommended Senior/Lead Response |
| :--- | :--- |
| **"How do you ensure thread safety in parallel execution?"** | *"We isolate our WebDriver instances using Java's `ThreadLocal<WebDriver>` within our `DriverManager`. When TestNG runs parallel methods across multiple threads, each thread accesses its own isolated browser session, preventing cross-thread pollution."* |
| **"How do you test asynchronous event streams in Kafka?"** | *"We never use hardcoded sleeps. Instead, we use `Awaitility` coupled with our `KafkaConsumerClient`. We poll the target topic with a configured timeout and polling interval, asserting against the event key and deserialized JSON payload within SLA."* |
| **"Why InfluxDB & Grafana instead of just Allure?"** | *"Allure is a post-execution static artifact. In enterprise CI/CD where hundreds of tests run in parallel, Grafana telemetry gives QA Leads live real-time visibility into suite velocity, failure rates, and API latency spikes before the run completes."* |
| **"How do you prevent DB connection exhaustion during parallel runs?"** | *"We implemented HikariCP as a managed connection pool with configured maximum pool sizes and connection timeouts, wrapped in a DAO pattern so test methods never manage raw JDBC connections."* |
