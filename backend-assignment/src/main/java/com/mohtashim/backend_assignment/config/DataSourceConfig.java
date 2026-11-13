package com.mohtashim.backend_assignment.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/assignment_db}")
    private String configuredUrl;

    @Value("${spring.datasource.username:postgres}")
    private String configuredUsername;

    @Value("${spring.datasource.password:Mohtashim}")
    private String configuredPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();

        String rawUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = System.getenv("DATABASE_URL");
        }
        if (rawUrl == null || rawUrl.isBlank()) {
            rawUrl = configuredUrl;
        }

        // Support cloud PostgreSQL URLs (e.g. Render: postgres://user:pass@host:port/dbname)
        if (rawUrl != null && (rawUrl.startsWith("postgres://") || rawUrl.startsWith("postgresql://"))) {
            try {
                URI uri = new URI(rawUrl);
                String userInfo = uri.getUserInfo();
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath();

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                dataSource.setJdbcUrl(jdbcUrl);

                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    dataSource.setUsername(parts[0]);
                    dataSource.setPassword(parts[1]);
                } else {
                    if (configuredUsername != null && !configuredUsername.isBlank()) {
                        dataSource.setUsername(configuredUsername);
                    }
                    if (configuredPassword != null && !configuredPassword.isBlank()) {
                        dataSource.setPassword(configuredPassword);
                    }
                }
            } catch (Exception e) {
                if (!rawUrl.startsWith("jdbc:")) {
                    rawUrl = "jdbc:" + rawUrl;
                }
                dataSource.setJdbcUrl(rawUrl);
                dataSource.setUsername(configuredUsername);
                dataSource.setPassword(configuredPassword);
            }
        } else {
            dataSource.setJdbcUrl(rawUrl);
            dataSource.setUsername(configuredUsername);
            dataSource.setPassword(configuredPassword);
        }

        if (dataSource.getJdbcUrl() != null && dataSource.getJdbcUrl().startsWith("jdbc:h2:")) {
            dataSource.setDriverClassName("org.h2.Driver");
        } else {
            dataSource.setDriverClassName("org.postgresql.Driver");
        }
        return dataSource;
    }
}
