package com.mohtashim.backend_assignment.dto.Responsedto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mohtashim.backend_assignment.entity.GithubRepository;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RepositoryResponseDto {

    private String message;
    private List<GithubRepository> repositories;


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