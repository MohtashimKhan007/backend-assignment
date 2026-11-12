package com.mohtashim.backend_assignment.dto.Responsedto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GithubResponse {
    private List<Item> items;

    @Data
    public static class Item {
        private Long id;
        private String name;
        private String description;
        private Owner owner;
        private String language;

        @JsonProperty("stargazers_count")
        private Integer stars;

        @JsonProperty("forks_count")
        private Integer forks;

        @JsonProperty("updated_at")
        private LocalDateTime lastUpdated;
    }

    @Data
    public static class Owner {
        private String login;
    }
}