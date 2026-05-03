package com.gym.crm.config;

import com.gym.crm.dao.helper.TransactionManager;
import com.gym.crm.test.helper.TestDbClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

import javax.sql.DataSource;

@Configuration
@Import({HibernateConfig.class, LiquibaseConfig.class, DatasourceConfig.class, TransactionManager.class})
public class DaoTestConfig {

    @Bean
    public JdbcClient jdbcClient(DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }

    @Bean
    public TestDbClient testDbClient(JdbcClient jdbcClient) {
        return new TestDbClient(jdbcClient);
    }

}
