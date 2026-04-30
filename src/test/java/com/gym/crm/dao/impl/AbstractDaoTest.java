package com.gym.crm.dao.impl;

import com.gym.crm.config.BaseDbIntegrationTest;
import com.gym.crm.config.DaoTestConfig;
import com.gym.crm.config.YamlPropertySourceFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(DaoTestConfig.class)
@TestPropertySource(locations = "classpath:application-test.yml", factory = YamlPropertySourceFactory.class)
@Sql(scripts = {"classpath:datasets/cleanup-all.sql", "classpath:datasets/seed-data.sql"})
abstract class AbstractDaoTest extends BaseDbIntegrationTest {

    @Autowired
    JdbcClient jdbcClient;

    @Test
    void shouldStartContainer() {
        assertTrue(MY_SQL_CONTAINER.isRunning());
    }

}
