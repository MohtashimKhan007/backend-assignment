package com.mohtashim.backend_assignment.controller;

import com.mohtashim.backend_assignment.dto.Requestdto.SearchRequest;
import com.mohtashim.backend_assignment.dto.Responsedto.RepositoryResponseDto;
import com.mohtashim.backend_assignment.entity.GithubRepository;
import com.mohtashim.backend_assignment.service.GithubService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GithubController {

    private final GithubService githubService;


    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @PostMapping("/search")
    public ResponseEntity<RepositoryResponseDto> fetchAndSave(@RequestBody SearchRequest request) {
        List<GithubRepository> repositories = githubService.fetchAndSaveRepositories(request);
        RepositoryResponseDto response = new RepositoryResponseDto("Repositories fetched and saved successfully", repositories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/repositories")
    public ResponseEntity<RepositoryResponseDto> getSavedRepositories(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Integer minStars,
            @RequestParam(required = false, defaultValue = "stars") String sort) {

        List<GithubRepository> repositories = githubService.getFilteredRepositories(language, minStars, sort);
        RepositoryResponseDto response = new RepositoryResponseDto(repositories);
        return ResponseEntity.ok(response);
    }
}