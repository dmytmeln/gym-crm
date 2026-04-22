package com.gym.crm;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.gym.crm")
@PropertySource("classpath:application.properties")
public class GymCrmApplication {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(GymCrmApplication.class);
        context.registerShutdownHook();
    }
}
