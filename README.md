# Gym CRM

![Build](https://github.com/dmytmeln/gym-crm/actions/workflows/ci.yml/badge.svg?branch=develop)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=dmytmeln_gym-crm&metric=coverage)](https://sonarcloud.io/summary/overall?id=dmytmeln_gym-crm)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=dmytmeln_gym-crm&metric=alert_status)](https://sonarcloud.io/summary/overall?id=dmytmeln_gym-crm)

## Prerequisites

* Git (2.40+)
* JDK 17
* Apache Maven (3.8+)
* MySQL Server (8.0+)
* Docker (Required only for running integration tests via Testcontainers)

## Quick Start Guide

### Step 1: Clone the Project

```bash
git clone https://github.com/dmytmeln/gym-crm.git
cd gym-crm
```

### Step 2: Set Up the Database

```sql
CREATE DATABASE gym_db;
CREATE USER 'gym'@'localhost' IDENTIFIED BY 'gym';
GRANT ALL PRIVILEGES ON gym_db.* TO 'gym'@'localhost';
FLUSH PRIVILEGES;
```

> Database tables and initial schemas will be generated automatically by Liquibase on application startup.

### Step 3: Build the Application

```bash
mvn clean compile
```

To build a packaged executable JAR and run all tests (requires Docker to be running):

```bash
mvn clean install
```

### Step 4: Run the Application

```bash
mvn exec:java -Dexec.mainClass=com.gym.crm.GymCrmApplication
```

Or using the Spring Boot plugin:

```bash
mvn spring-boot:run
```

The application runs on port 8080 under the context path `/gym-crm`.

* Base API Path: `http://localhost:8080/gym-crm/api/v1`
* OpenAPI / Swagger UI: `http://localhost:8080/gym-crm/swagger-ui/index.html`

## Running Tests

To run all unit and integration tests (requires Docker to be running):

```bash
mvn test
```

To run a specific test class:

```bash
mvn test -Dtest=GymCrmApplicationTest
```