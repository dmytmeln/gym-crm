package com.gym.crm.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatasourceConfig {

    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;
    private int maxPoolSize;
    private int minIdle;
    private long connectionTimeout;
    private long idleTimeout;
    private long maxLifetime;

    @Value("${datasource.jdbc-url}")
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    @Value("${datasource.username}")
    public void setUsername(String username) {
        this.username = username;
    }

    @Value("${datasource.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    @Value("${datasource.driver-class-name}")
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @Value("${datasource.hikari.max-pool-size:10}")
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    @Value("${datasource.hikari.min-idle:2}")
    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    @Value("${datasource.hikari.connection-timeout:30000}")
    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Value("${datasource.hikari.idle-timeout:600000}")
    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    @Value("${datasource.hikari.max-lifetime:1800000}")
    public void setMaxLifetime(long maxLifetime) {
        this.maxLifetime = maxLifetime;
    }

    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);

        return new HikariDataSource(config);
    }

}
