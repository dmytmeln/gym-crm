# Gym CRM

![Build](https://github.com/dmytmeln/gym-crm/actions/workflows/ci.yml/badge.svg?branch=develop)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=dmytmeln_gym-crm&metric=coverage)](https://sonarcloud.io/summary/overall?id=dmytmeln_gym-crm)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=dmytmeln_gym-crm&metric=alert_status)](https://sonarcloud.io/summary/overall?id=dmytmeln_gym-crm)

## Prerequisites

- **Java Development Kit (JDK) 17**
- **Maven**
- **Git**
- **MySQL**

## Setup Instructions

Run the following script to create the database and user:

```sql
CREATE DATABASE gym_db;
CREATE USER 'gym'@'localhost' WITH IDENTIFIED BY 'gym';
GRANT ALL PRIVILEGES ON gym_db.* TO 'gym'@'localhost';
```

## Running the Application

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass=com.gym.crm.GymCrmApplication
```