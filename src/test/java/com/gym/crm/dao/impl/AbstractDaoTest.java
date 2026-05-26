package com.gym.crm.dao.impl;

import com.gym.crm.config.BaseDbIntegrationTest;
import com.gym.crm.config.DaoTestConfig;
import com.gym.crm.test.helper.TestDbClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(DaoTestConfig.class)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:datasets/cleanup-all.sql", "classpath:datasets/seed-data.sql"})
abstract class AbstractDaoTest<T> extends BaseDbIntegrationTest {

    @Autowired
    TestDbClient testDbClient;

    @Autowired
    T dao;

    @Test
    void shouldStartContainer() {
        assertTrue(MY_SQL_CONTAINER.isRunning());
    }

}
