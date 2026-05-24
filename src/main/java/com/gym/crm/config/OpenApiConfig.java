package com.gym.crm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GCA API")
                        .version("1.0.0")
                        .description("API for managing trainees, their profiles, trainers, and training schedules")
                        .contact(new Contact().name("Gym CRM Team")))
                .servers(List.of(new Server().url("/gym-crm")));
    }

    @Bean
    public SpringDocConfigProperties springDocConfigProperties() {
        SpringDocConfigProperties properties = new SpringDocConfigProperties();
        properties.getApiDocs().setPath("/api-docs");

        return properties;
    }

    @Bean
    public SwaggerUiOAuthProperties swaggerUiOAuthProperties() {
        return new SwaggerUiOAuthProperties();
    }

    @Bean
    public SwaggerUiConfigProperties swaggerUiConfigProperties() {
        SwaggerUiConfigProperties properties = new SwaggerUiConfigProperties();
        properties.setConfigUrl("/gym-crm/v3/api-docs/swagger-config");
        properties.setUrl("/gym-crm/v3/api-docs");

        return properties;
    }

    @Bean
    public SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigProperties props) {
        return new SwaggerUiConfigParameters(props);
    }

}