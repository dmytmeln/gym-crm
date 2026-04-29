package com.gym.crm.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {

    private String changeLogPath;

    @Value("${liquibase.change-log}")
    public void setChangeLogPath(String changeLogPath) {
        this.changeLogPath = changeLogPath;
    }

    @Bean("liquibase")
    public SpringLiquibase liquibase(DataSource dataSource) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(changeLogPath);
        liquibase.setDataSource(dataSource);

        return liquibase;
    }

}