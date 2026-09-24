# Multi-stage Dockerfile for Fintech Test Automation Suite
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Cache Maven dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and test assets
COPY src ./src

# Test runner image
FROM maven:3.9.6-eclipse-temurin-17-alpine

WORKDIR /app
COPY --from=builder /root/.m2 /root/.m2
COPY --from=builder /app /app

# Default command to run TestNG suite
ENTRYPOINT ["mvn", "test"]
CMD ["-DsuiteXmlFile=src/test/resources/testng.xml"]
