# Gym CRM

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