package org.gymcrm;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.gymcrm")
public class GymCrmApplication {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(GymCrmApplication.class);
        context.registerShutdownHook();
    }
}
