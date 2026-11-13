package com.mohtashim.backend_assignment.dto.Responsedto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mohtashim.backend_assignment.entity.GithubRepository;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper containing repository list and status message")
public class RepositoryResponseDto {

    @Schema(description = "Response status message", example = "Repositories fetched and saved successfully")
    private String message;

    @Schema(description = "List of repositories retrieved or persisted")
    private List<GithubRepository> repositories;

    public RepositoryResponseDto() {
    }

    public RepositoryResponseDto(String message, List<GithubRepository> repositories) {
        this.message = message;
        this.repositories = repositories;
    }

    public RepositoryResponseDto(List<GithubRepository> repositories) {
        this.repositories = repositories;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<GithubRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<GithubRepository> repositories) {
        this.repositories = repositories;
    }
}