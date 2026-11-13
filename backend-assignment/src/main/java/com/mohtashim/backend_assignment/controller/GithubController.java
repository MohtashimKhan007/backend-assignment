package com.mohtashim.backend_assignment.controller;

import com.mohtashim.backend_assignment.dto.Requestdto.SearchRequest;
import com.mohtashim.backend_assignment.dto.Responsedto.RepositoryResponseDto;
import com.mohtashim.backend_assignment.entity.GithubRepository;
import com.mohtashim.backend_assignment.service.GithubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/github")
@Tag(name = "GitHub Repositories", description = "Endpoints for searching GitHub repositories, upserting to database, and querying filtered results")
public class GithubController {

    private final GithubService githubService;

    public GithubController(GithubService githubService) {
        this.githubService = githubService;
    }

    @PostMapping("/search")
    @Operation(
            summary = "Search GitHub repositories and persist to database",
            description = "Fetches repositories from GitHub REST API matching criteria and saves/updates (upserts) them in the PostgreSQL database."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Repositories fetched and saved successfully",
                    content = @Content(schema = @Schema(implementation = RepositoryResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body or validation error"),
            @ApiResponse(responseCode = "404", description = "No repositories found on GitHub matching criteria"),
            @ApiResponse(responseCode = "429", description = "GitHub API rate limit exceeded"),
            @ApiResponse(responseCode = "502", description = "Error communicating with GitHub API")
    })
    public ResponseEntity<RepositoryResponseDto> fetchAndSave(@Valid @RequestBody SearchRequest request) {
        List<GithubRepository> repositories = githubService.fetchAndSaveRepositories(request);
        RepositoryResponseDto response = new RepositoryResponseDto("Repositories fetched and saved successfully", repositories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/repositories")
    @Operation(
            summary = "Retrieve stored repositories with filtering and sorting",
            description = "Retrieves stored repository records from PostgreSQL with optional filtering by programming language, minimum stars, and sorting."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Repositories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RepositoryResponseDto.class)))
    })
    public ResponseEntity<RepositoryResponseDto> getSavedRepositories(
            @Parameter(description = "Filter by programming language (case-insensitive)", example = "Java")
            @RequestParam(required = false) String language,

            @Parameter(description = "Filter by minimum number of stars", example = "100")
            @RequestParam(required = false) Integer minStars,

            @Parameter(description = "Sort by: 'stars', 'forks', or 'updated' (default is 'stars')", example = "stars")
            @RequestParam(required = false, defaultValue = "stars") String sort) {

        List<GithubRepository> repositories = githubService.getFilteredRepositories(language, minStars, sort);
        RepositoryResponseDto response = new RepositoryResponseDto(repositories);
        return ResponseEntity.ok(response);
    }
}