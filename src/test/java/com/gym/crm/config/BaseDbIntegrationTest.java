package com.gym.crm.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

public abstract class BaseDbIntegrationTest {

    protected static final MySQLContainer<?> MY_SQL_CONTAINER;

    static {
        MY_SQL_CONTAINER = new MySQLContainer<>("mysql:8.0");
        MY_SQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void setMySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("datasource.jdbc-url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }

}
