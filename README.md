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
CREATE USER gym WITH PASSWORD 'gym';
GRANT ALL PRIVILEGES ON DATABASE gym_db TO gym;
```

## Running the Application

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass=com.gym.crm.GymCrmApplication
```