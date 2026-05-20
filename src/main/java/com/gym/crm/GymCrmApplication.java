package com.gym.crm;

import com.gym.crm.config.WebConfig;
import com.gym.crm.config.YamlPropertySourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(
        basePackages = "com.gym.crm",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = WebConfig.class),
                @ComponentScan.Filter(type = FilterType.ANNOTATION, value = RestController.class)
        }
)
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class GymCrmApplication {
    public static void main(String[] args) {
        try {
            var context = new AnnotationConfigApplicationContext(GymCrmApplication.class);
            context.registerShutdownHook();
        } catch (Exception e) {
            log.error("Fatal error during application context startup", e);
        }
    }
}
