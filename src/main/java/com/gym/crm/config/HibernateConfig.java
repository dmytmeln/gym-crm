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
import org.hibernate.service.ServiceRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public SessionFactory sessionFactory(DataSource dataSource) {
        ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.JAKARTA_JTA_DATASOURCE, dataSource)
                .applySetting(AvailableSettings.HBM2DDL_AUTO, hbm2ddlAuto)
                .applySetting(AvailableSettings.SHOW_SQL, showSql)
                .applySetting(AvailableSettings.FORMAT_SQL, formatSql)
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
