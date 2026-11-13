package com.mohtashim.backend_assignment.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "github_repositories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "GitHub Repository entity stored in database")
public class GithubRepository {

    @Id
    @Schema(description = "Unique GitHub Repository ID", example = "123456")
    private Long id;

    @Schema(description = "Repository name", example = "spring-boot-example")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Repository description", example = "An example repository for Spring Boot")
    private String description;

    @JsonProperty("owner")
    @Column(name = "owner_name")
    @Schema(description = "Owner login / username", example = "user123")
    private String ownerName;

    @Schema(description = "Programming language", example = "Java")
    private String language;

    @Schema(description = "Count of stars", example = "450")
    private Integer stars;

    @Schema(description = "Count of forks", example = "120")
    private Integer forks;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    @Column(name = "last_updated")
    @Schema(description = "Last updated timestamp in ISO-8601 UTC format", example = "2024-01-01T12:00:00Z")
    private LocalDateTime lastUpdated;
}