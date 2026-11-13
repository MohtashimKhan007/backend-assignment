package com.mohtashim.backend_assignment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
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
                        new Server().url("/").description("Default Server")
                ))
                .info(new Info()
                        .title("GitHub Repository Searcher API")
                        .version("1.0.0")
                        .description("REST API for searching GitHub repositories, persisting records in PostgreSQL, and querying stored repositories with filtering and sorting.")
                        .contact(new Contact()
                                .name("Mohtashim Khan")
                                .email("manazkhanjhn786@gmail.com")
                                .url("https://github.com/MohtashimKhan007")));
    }
}
