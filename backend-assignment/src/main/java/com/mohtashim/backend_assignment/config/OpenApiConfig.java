package com.mohtashim.backend_assignment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("/").description("Default Server URL")
                ))
                .info(new Info()
                        .title("GitHub Repository Searcher & Manager API")
                        .version("1.0.0")
                        .description("Production-grade Spring Boot REST API that queries GitHub repositories, upserts them into PostgreSQL, and exposes filtering and sorting endpoints.")
                        .contact(new Contact()
                                .name("Mohtashim Khan")
                                .email("manazkhanjhn786@gmail.com")
                                .url("https://github.com/MohtashimKhan007"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://spring.io/")));
    }
}
