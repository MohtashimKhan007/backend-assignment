package com.mohtashim.backend_assignment.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "github_repositories")
@Data
public class GithubRepository {
    @Id
    private Long id;
    private String name;
    private String description;
    @JsonProperty("owner")
    private String ownerName;
    private String language;
    private Integer stars;
    private Integer forks;
    private LocalDateTime lastUpdated;
}