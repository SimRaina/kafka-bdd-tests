# Kafka BDD Tests - Customer Data Enrichment Pipeline

A comprehensive Spring Boot project demonstrating a **Behavior-Driven Development (BDD)** approach to testing a data pipeline that ingests customer data via Apache Kafka, persists it in an H2 database, and performs enrichment operations using batch jobs.

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture & Workflow](#architecture--workflow)
3. [Technologies & Dependencies](#technologies--dependencies)
4. [Project Structure](#project-structure)
5. [Theory & Concepts](#theory--concepts)
6. [Setup & Installation](#setup--installation)
7. [Running Tests](#running-tests)
8. [Test Scenarios](#test-scenarios)
9. [Key Components](#key-components)
10. [Database Schema](#database-schema)
11. [Configuration](#configuration)
12. [Troubleshooting](#troubleshooting)

---

## 🎯 Project Overview

This project showcases a **customer data ingestion and enrichment pipeline** using industry-standard technologies:

- **Kafka** for asynchronous message streaming
- **AVRO** for schema-based serialization
- **H2 Database** for in-memory data persistence (testing environment)
- **Cucumber & BDD** for behavior-driven test scenarios
- **Spring Boot** as the application framework

The project demonstrates two primary workflows:

1. **Data Ingestion**: Customer data is produced in AVRO format, sent through Kafka topics, consumed, and saved to the database
2. **Data Enrichment**: Persisted customer data is enriched (name transformation) via a batch job and saved to a separate enriched table

---

## 🏗️ Architecture & Workflow

### Workflow Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    SCENARIO 1: INGESTION                     │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────┐      ┌──────────┐      ┌─────────────┐    │
│  │ AVRO        │      │ Kafka    │      │ H2 Database │    │
│  │ Message     │ ──→  │ Topic    │ ──→  │ CUSTOMER    │    │
│  │ Builder     │      │ customer │      │ Table       │    │
│  │             │      │ -data    │      │             │    │
│  └─────────────┘      └──────────┘      └─────────────┘    │
│   (AVRO Schema)                                              │
│                                                               │
├─────────────────────────────────────────────────────────────┤
│                    SCENARIO 2: ENRICHMENT                    │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌─────────────────┐      ┌──────────────────┐              │
│  │ CUSTOMER Table  │      │ Enrichment Job   │              │
│  │ (Source Data)   │ ──→  │ (Name Transform) │ ──→  Save    │
│  │                 │      │ (UPPERCASE)      │      to      │
│  └─────────────────┘      └──────────────────┘   CUSTOMER_  │
│                                                   ENRICHED   │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

### Data Flow Details

**Phase 1: Message Production**
- Customer data is structured using an AVRO schema (JSON-based format specification)
- Data includes: `customerId` and `name` fields
- AVRO ensures schema consistency and efficient serialization

**Phase 2: Kafka Topic Streaming**
- Messages are published to the `customer-data` Kafka topic
- Kafka acts as a message broker, enabling decoupled communication
- Topic ensures messages are available for consumption by multiple consumers

**Phase 3: Message Consumption**
- A test consumer reads messages from the Kafka topic
- Deserialization is handled using the AVRO schema
- Raw message data is extracted and made available for database insertion

**Phase 4: Database Persistence**
- Consumed data is inserted into the `CUSTOMER` table
- Each record maintains customer ID and name information

**Phase 5: Enrichment Processing**
- The `CustomerEnrichmentJob` reads all records from the `CUSTOMER` table
- Applies transformation logic (e.g., converting name to uppercase)
- Inserts enriched data into the `CUSTOMER_ENRICHED` table

**Phase 6: Validation**
- Test assertions verify:
  - Data was inserted correctly
  - Enrichment job processed the correct number of records
  - Transformed data (e.g., uppercase name) is correct

---

## 🛠️ Technologies & Dependencies

### Core Framework
- **Spring Boot 4.0.1** - Application framework with embedded servers and auto-configuration
- **Java 17** - Programming language (supports modern features like records, sealed classes)

### Message Streaming
- **Apache Kafka 4.1.1** - Distributed message broker for asynchronous communication
- **Spring Kafka Test** - Embedded Kafka broker for integration testing
- **Apache AVRO 1.11.4** - Schema-based serialization format with code generation

### Database
- **H2 Database** - In-memory relational database perfect for testing
- **Spring JDBC** - Template-based database access without ORM overhead

### Testing & BDD
- **Cucumber 7.18.0** - BDD test framework using Gherkin language
  - `cucumber-java` - Allows step definition in Java
  - `cucumber-spring` - Spring integration for dependency injection
  - `cucumber-junit-platform-engine` - JUnit 5 integration
- **JUnit 5.11.4** - Testing framework (Jupiter engine)
- **Awaitility 4.3.0** - Utility for testing asynchronous operations with timeouts

### Build & Plugins
- **Maven 3.x** - Build automation and dependency management
- **Maven Surefire Plugin 3.5.4** - Runs tests during build phase
- **Spring Boot Maven Plugin** - Simplifies Spring Boot application building

---

## 📁 Project Structure

```
kafka-bdd-tests/
├── src/
│   ├── main/
│   │   └── java/com/bank/
│   │       ├── Application.java              # Spring Boot entry point
│   │       ├── CommandLineJobRunner.java     # CLI job execution
│   │       ├── jobs/
│   │       │   └── CustomerEnrichmentJob.java # Core enrichment logic
│   │       └── model/
│   │           └── Customer.java             # Data model
│   │
│   └── test/
│       ├── java/com/bank/bdd/
│       │   ├── config/
│       │   │   └── CucumberSpringConfig.java # Test configuration
│       │   ├── consumer/
│       │   │   └── TestKafkaConsumer.java    # Kafka message consumer
│       │   ├── hooks/
│       │   │   ├── EmbeddedKafkaHook.java    # Kafka setup/teardown
│       │   │   └── DBCleanUpHook.java        # Database cleanup
│       │   ├── producer/
│       │   │   ├── AvroMessageBuilder.java   # AVRO message creation
│       │   │   └── EmbeddedKafkaProducer.java # Message publishing
│       │   ├── runner/
│       │   │   └── CucumberTest.java         # Test runner
│       │   ├── stepdefs/
│       │   │   ├── DbSteps.java              # Database step definitions
│       │   │   ├── EnrichmentSteps.java      # Enrichment step definitions
│       │   │   ├── KafkaSteps.java           # Kafka step definitions
│       │   │   └── ScenarioContext.java      # Shared test context
│       │   └── support/
│       │       ├── ConcreteEmbeddedKafkaBroker.java
│       │       ├── EmbeddedKafkaBrokerHolder.java
│       │       └── H2Database.java
│       │
│       └── resources/
│           ├── application-test.properties   # Test config (H2, Kafka)
│           ├── schema.sql                    # Database table definitions
│           ├── avro/
│           │   └── Customer.avsc             # AVRO schema definition
│           └── features/
│               └── customer.feature          # BDD test scenarios
│
├── pom.xml                                   # Maven project configuration
├── mvnw & mvnw.cmd                           # Maven wrapper scripts
└── README.md                                 # This file
```

---

## 📚 Theory & Concepts

### 1. Behavior-Driven Development (BDD)

**Definition**: A software development approach that bridges the gap between business stakeholders and technical teams by expressing requirements in human-readable language.

**Key Benefits**:
- Requirements are self-documenting
- Tests serve as living documentation
- Business stakeholders can understand test scenarios
- Reduces miscommunication between teams

**In This Project**:
- Feature files (`.feature`) describe business scenarios in Gherkin language
- Step definitions map Gherkin sentences to Java code execution
- Each scenario is executable and testable

### 2. Apache Kafka

**Definition**: A distributed event streaming platform designed for high-throughput, low-latency data pipelines.

**Key Concepts**:
- **Topic**: A logical channel where messages are published (e.g., `customer-data`)
- **Producer**: Application that sends messages to a topic
- **Consumer**: Application that reads messages from a topic
- **Partition**: Allows parallel processing and scalability
- **Broker**: Server that manages topics and message replication
- **Consumer Group**: Multiple consumers sharing workload for a topic

**Why Kafka for This Project?**
- Decouples data producers from consumers
- Enables asynchronous, scalable data pipelines
- Provides message durability and replay capabilities
- Ideal for event-driven architectures

### 3. AVRO Serialization

**Definition**: A binary data serialization format that includes a schema definition, ensuring data structure consistency.

**Key Benefits**:
- **Schema Evolution**: Supports backward/forward compatibility
- **Compact Size**: Binary format is more space-efficient than JSON
- **Language Agnostic**: Generated code supports multiple languages
- **Data Validation**: Ensures messages conform to the schema

**AVRO Schema in This Project**:
```json
{
  "type": "record",
  "name": "Customer",
  "namespace": "com.bank",
  "fields": [
    { "name": "customerId", "type": "string" },
    { "name": "name", "type": "string" }
  ]
}
```

This schema defines a `Customer` record with two string fields.

### 4. H2 Database

**Definition**: A lightweight, in-memory relational database engine perfect for development and testing.

**Why H2 for Testing?**
- Fast: Runs entirely in memory (no disk I/O)
- Zero Configuration: Embedded in applications
- SQL Support: Full SQL compatibility
- Isolation: Each test can have a fresh database instance

**In This Project**:
- Configured with in-memory mode: `jdbc:h2:mem:testdb`
- Automatically initialized with `schema.sql` definitions
- Each test run gets a clean database state

### 5. Spring Boot Integration

**Definition**: Framework simplifying Java application development with sensible defaults and auto-configuration.

**Key Features Used**:
- Auto-configuration of DataSource and JdbcTemplate
- Embedded Kafka support for testing
- Property-based configuration management
- Dependency injection via Spring Context

### 6. Spring JDBC Template

**Definition**: Spring's abstraction layer for database access with minimal boilerplate code.

**Advantages Over Raw JDBC**:
- Automatic connection management
- Exception translation
- Query/Update methods with callbacks
- Type-safe parameter binding

**Usage in This Project**:
```java
List<Customer> customers = jdbcTemplate.query(
    "SELECT CUSTOMER_ID, NAME FROM CUSTOMER",
    (rs, rowNum) -> new Customer(rs.getString("CUSTOMER_ID"), rs.getString("NAME"))
);
```

---

## ⚙️ Setup & Installation

### Prerequisites

- **Java 17 or higher** - [Download](https://www.oracle.com/java/technologies/downloads/)
- **Maven 3.6+** - [Download](https://maven.apache.org/download.cgi) (or use included Maven Wrapper)
- **Git** (optional) - For version control

### Installation Steps

1. **Clone or Download the Project**
   ```powershell
   cd C:\Users\simra\Desktop\kafka-bdd-tests
   ```

2. **Install Dependencies**
   Using Maven Wrapper (included):
   ```powershell
   .\mvnw.cmd clean install
   ```
   
   Or if Maven is installed globally:
   ```powershell
   mvn clean install
   ```

3. **Verify Installation**
   ```powershell
   .\mvnw.cmd --version
   ```

### Understanding Maven Wrapper

The `mvnw.cmd` (Windows) and `mvnw` (Unix) files allow you to run Maven without having it installed globally:
- Useful for CI/CD pipelines
- Ensures consistent Maven version across team
- Automatically downloads required Maven version

---

## 🧪 Running Tests

### Run All Tests

```powershell
.\mvnw.cmd test
```

### Run Specific Test Class

```powershell
.\mvnw.cmd test -Dtest=CucumberTest
```

### Run with Verbose Output

```powershell
.\mvnw.cmd test -X
```

### Run Tests with Maven (Global Installation)

```powershell
mvn clean test
```

### Expected Test Output

```
[INFO] -------------------------------------------------------
[INFO] T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.bank.bdd.runner.CucumberTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 🎬 Test Scenarios

### Scenario 1: Customer Data Ingestion and Persistence

**File**: `src/test/resources/features/customer.feature`

```gherkin
@setup
Feature: Kafka customer ingestion

  Scenario Outline: Customer data is consumed and persisted
    Given a customer Avro message is produced
    And the customer message is processed
    Then customer "<customerId>" should exist in database

    Examples:
      | customerId |
      | CUST1      |
```

**What Happens**:
1. **Given**: An AVRO-formatted customer message is created with a unique customer ID and name
2. **And**: The message is published to the Kafka `customer-data` topic and consumed
3. **Then**: The test verifies the customer record exists in the H2 database CUSTOMER table

**Step Definitions Location**: `src/test/java/com/bank/bdd/stepdefs/KafkaSteps.java`

### Scenario 2: Customer Data Enrichment

```gherkin
Scenario: Enriched customer data is created from base customer table
  Given valid customer events are processed
  When enrichment job is triggered
  Then enriched customer table should have 1 record
  And enriched customer name should be "JOHN"
```

**What Happens**:
1. **Given**: Customer data is ingested and stored in the CUSTOMER table
2. **When**: The CustomerEnrichmentJob is executed
3. **Then**: The job:
   - Reads all records from CUSTOMER table
   - Transforms the name to uppercase (e.g., "John" → "JOHN")
   - Inserts transformed records into CUSTOMER_ENRICHED table
4. **And**: Test verifies the enriched name is in uppercase

**Step Definitions Location**: `src/test/java/com/bank/bdd/stepdefs/EnrichmentSteps.java`

---

## 🔑 Key Components

### 1. Customer Model

**File**: `src/main/java/com/bank/model/Customer.java`

Simple POJO representing customer data with ID and name fields.

```java
public class Customer {
    private final String id;
    private final String name;
    // Getters...
}
```

### 2. CustomerEnrichmentJob

**File**: `src/main/java/com/bank/jobs/CustomerEnrichmentJob.java`

Core business logic for enrichment:
- Queries all customers from CUSTOMER table
- Transforms name to uppercase
- Inserts enriched records into CUSTOMER_ENRICHED table
- Returns count of processed records

**Key Method**:
```java
public int run() {
    // 1. Read customers from CUSTOMER table
    List<Customer> customers = jdbcTemplate.query(
        "SELECT CUSTOMER_ID, NAME FROM CUSTOMER",
        (rs, rowNum) -> new Customer(rs.getString("CUSTOMER_ID"), rs.getString("NAME"))
    );

    // 2. Enrich each customer
    for (Customer c : customers) {
        String enrichedName = c.getName().toUpperCase();
        
        // 3. Save enriched data
        jdbcTemplate.update(
            "INSERT INTO CUSTOMER_ENRICHED (customer_id, enriched_name) VALUES (?, ?)",
            c.getId(),
            enrichedName
        );
    }

    return customers.size();
}
```

### 3. AVRO Message Builder

**File**: `src/test/java/com/bank/bdd/producer/AvroMessageBuilder.java`

Constructs AVRO-formatted messages for testing:
- Loads schema from `Customer.avsc`
- Builds customer records conforming to schema
- Provides schema for serialization/deserialization

### 4. Kafka Producer & Consumer

**Producer**: `src/test/java/com/bank/bdd/producer/EmbeddedKafkaProducer.java`
- Publishes messages to Kafka topics
- Handles AVRO serialization
- Manages producer configuration

**Consumer**: `src/test/java/com/bank/bdd/consumer/TestKafkaConsumer.java`
- Subscribes to topics
- Deserializes AVRO messages
- Extracts data for database insertion

### 5. Hooks

**Purpose**: Setup and teardown logic for test scenarios

**EmbeddedKafkaHook**:
- Initializes embedded Kafka broker before tests
- Cleans up resources after tests
- Provides connection details to test components

**DBCleanUpHook**:
- Clears database tables between scenarios
- Ensures test isolation
- Prevents data leakage between test runs

### 6. Scenario Context

**File**: `src/test/java/com/bank/bdd/stepdefs/ScenarioContext.java`

Shared context between step definitions:
- Stores current customer ID for reference across steps
- Maintains state during test execution
- Enables communication between different step classes

---

## 🗄️ Database Schema

### CUSTOMER Table
```sql
CREATE TABLE IF NOT EXISTS CUSTOMER (
    CUSTOMER_ID VARCHAR(20) PRIMARY KEY,
    NAME VARCHAR(100)
);
```

**Purpose**: Stores raw customer data ingested from Kafka

**Columns**:
- `CUSTOMER_ID`: Unique identifier (Primary Key)
- `NAME`: Customer name

### CUSTOMER_ENRICHED Table
```sql
CREATE TABLE IF NOT EXISTS CUSTOMER_ENRICHED (
    CUSTOMER_ID VARCHAR(20) PRIMARY KEY,
    ENRICHED_NAME VARCHAR(100)
);
```

**Purpose**: Stores enriched/transformed customer data

**Columns**:
- `CUSTOMER_ID`: Reference to original customer
- `ENRICHED_NAME`: Transformed name (e.g., uppercase)

---

## ⚙️ Configuration

### Test Configuration

**File**: `src/test/resources/application-test.properties`

```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
```

**Configuration Breakdown**:
- `jdbc:h2:mem:testdb` - In-memory H2 database named "testdb"
- `DB_CLOSE_DELAY=-1` - Don't close DB connection immediately
- `DB_CLOSE_ON_EXIT=TRUE` - Close DB when JVM exits
- `create-drop` - Create schema at startup, drop at shutdown

### Cucumber Configuration

**File**: `src/test/java/com/bank/bdd/config/CucumberSpringConfig.java`

Enables Spring context integration with Cucumber:
- Loads Spring Application Context for dependency injection
- Allows `@Autowired` beans in step definitions
- Manages bean lifecycle during test execution

### Maven Surefire Plugin

**In pom.xml**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.5.4</version>
    <configuration>
        <useModulePath>false</useModulePath>
    </configuration>
</plugin>
```

**Purpose**: Executes tests during Maven `test` phase

**Configuration**:
- `useModulePath=false` - Needed for Java 17+ compatibility with classpath scanning

---

## 🔧 Troubleshooting

### Issue: "Port Already in Use"

**Cause**: Kafka broker port is occupied

**Solution**:
```powershell
# Find process using port 9092 (or relevant port)
netstat -ano | findstr :9092

# Kill the process
taskkill /PID <PID> /F
```

### Issue: "Schema Not Found"

**Cause**: AVRO schema file not in classpath

**Solution**:
- Verify `Customer.avsc` is in `src/test/resources/avro/`
- Run `mvn clean compile` to regenerate resources
- Check test configuration path in AvroMessageBuilder

### Issue: "Database Lock Error"

**Cause**: Previous test didn't cleanup database properly

**Solution**:
```powershell
# Clear Maven cache and rebuild
.\mvnw.cmd clean
.\mvnw.cmd test
```

### Issue: "Consumer Timeout"

**Cause**: Kafka message not published or consumed within expected time

**Solution**:
- Increase awaitility timeout in test properties
- Verify Kafka broker is running (check EmbeddedKafkaHook initialization)
- Check consumer group ID is unique (prevents offset conflicts)

### Issue: "ClassNotFoundException"

**Cause**: Dependencies not resolved

**Solution**:
```powershell
# Update Maven cache
.\mvnw.cmd dependency:resolve

# Clean and rebuild
.\mvnw.cmd clean install -U
```

### Issue: Cucumber Scenarios Not Detected

**Cause**: Feature files not in correct location or path configuration missing

**Solution**:
- Verify feature files are in `src/test/resources/features/`
- Check `@CucumberOptions` in `CucumberTest.java` for correct path
- Ensure step definitions are in packages configured in `CucumberSpringConfig.java`

---

## 📊 Running Individual Scenarios

To run a specific Cucumber scenario using tags:

```powershell
# Run only @setup tagged scenarios
.\mvnw.cmd test -Dgroups="@setup"

# Run all scenarios
.\mvnw.cmd test
```

Add tags to feature files and use Cucumber filtering to control test execution.

---

