package com.gym.crm.config;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
public class HibernateConfig {

    private String hbm2ddlAuto;
    private String showSql;
    private String formatSql;

    @Value("${hibernate.hbm2ddl-auto}")
    public void setHbm2ddlAuto(String hbm2ddlAuto) {
        this.hbm2ddlAuto = hbm2ddlAuto;
    }

    @Value("${hibernate.show-sql}")
    public void setShowSql(String showSql) {
        this.showSql = showSql;
    }

    @Value("${hibernate.format-sql}")
    public void setFormatSql(String formatSql) {
        this.formatSql = formatSql;
    }

    @Bean(destroyMethod = "close")
    @DependsOn("liquibase")
    public SessionFactory sessionFactory(DataSource dataSource) {
        ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting(JdbcSettings.JAKARTA_JTA_DATASOURCE, dataSource)
                .applySetting(SchemaToolingSettings.HBM2DDL_AUTO, hbm2ddlAuto)
                .applySetting(JdbcSettings.SHOW_SQL, showSql)
                .applySetting(JdbcSettings.FORMAT_SQL, formatSql)
                .applySetting(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread")
                .build();

        Metadata metadata = new MetadataSources(registry)
                .addAnnotatedClass(Trainee.class)
                .addAnnotatedClass(Trainer.class)
                .addAnnotatedClass(Training.class)
                .addAnnotatedClass(TrainingType.class)
                .addAnnotatedClass(User.class)
                .buildMetadata();

        return metadata.buildSessionFactory();
    }

}
