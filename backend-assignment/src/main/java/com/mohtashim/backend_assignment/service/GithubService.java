package com.mohtashim.backend_assignment.service;

import com.mohtashim.backend_assignment.dto.Requestdto.SearchRequest;
import com.mohtashim.backend_assignment.dto.Responsedto.GithubResponse;
import com.mohtashim.backend_assignment.entity.GithubRepository;
import com.mohtashim.backend_assignment.exception.ApiException;
import com.mohtashim.backend_assignment.repository.GithubRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class GithubService {

    private final GithubRepo githubRepo;
    private final RestTemplate restTemplate;

    @Value("${github.token:}")
    private String githubToken;

    public GithubService(GithubRepo githubRepo, RestTemplate restTemplate) {
        this.githubRepo = githubRepo;
        this.restTemplate = restTemplate;
    }

    public List<GithubRepository> fetchAndSaveRepositories(SearchRequest request) {
        if (request == null || request.getQuery() == null || request.getQuery().trim().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Search query is required and cannot be empty.");
        }

        String searchQuery = request.getQuery().trim();
        if (request.getLanguage() != null && !request.getLanguage().isBlank()) {
            searchQuery = searchQuery + " language:" + request.getLanguage().trim().toLowerCase();
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("https://api.github.com/search/repositories")
                .queryParam("q", searchQuery);

        if (request.getSort() != null && !request.getSort().isBlank()) {
            uriBuilder.queryParam("sort", request.getSort().trim().toLowerCase())
                    .queryParam("order", "desc");
        }

        // GitHub API strictly requires User-Agent header
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "GitHub-Repository-Searcher-App");
        headers.set("Accept", "application/vnd.github.v3+json");

        String token = (githubToken != null && !githubToken.isBlank()) ? githubToken : System.getenv("GITHUB_TOKEN");
        if (token != null && !token.isBlank()) {
            headers.set("Authorization", "Bearer " + token);
        }

        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        URI targetUri = uriBuilder.build().encode().toUri();

        try {
            ResponseEntity<GithubResponse> responseEntity = restTemplate.exchange(
                    targetUri,
                    HttpMethod.GET,
                    requestEntity,
                    GithubResponse.class
            );

            GithubResponse response = responseEntity.getBody();
            if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                throw new ApiException(HttpStatus.NOT_FOUND, "No repositories found on GitHub for the given criteria.");
            }

            List<GithubRepository> entities = new ArrayList<>();
            for (GithubResponse.Item item : response.getItems()) {
                GithubRepository repoEntity = new GithubRepository();
                repoEntity.setId(item.getId());
                repoEntity.setName(item.getName());
                repoEntity.setDescription(item.getDescription());
                repoEntity.setOwnerName(item.getOwner() != null ? item.getOwner().getLogin() : "Unknown");
                repoEntity.setLanguage(item.getLanguage());
                repoEntity.setStars(item.getStars() != null ? item.getStars() : 0);
                repoEntity.setForks(item.getForks() != null ? item.getForks() : 0);
                repoEntity.setLastUpdated(item.getLastUpdated());

                entities.add(repoEntity);
            }

            // Upsert: Spring Data JPA saves or updates existing records by primary key (id)
            return githubRepo.saveAll(entities);

        } catch (ApiException e) {
            throw e;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN || e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "GitHub API rate limit exceeded. Please wait a moment before trying again or configure a GITHUB_TOKEN.");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Resource not found on GitHub.");
            }
            throw new ApiException(HttpStatus.valueOf(e.getStatusCode().value()), "GitHub API Error: " + e.getStatusText());
        } catch (RestClientException e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Failed to communicate with GitHub API: " + e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred while processing repositories: " + e.getMessage());
        }
    }

    public List<GithubRepository> getFilteredRepositories(String language, Integer minStars, String sort) {
        Sort sortObj;
        if ("forks".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "forks");
        } else if ("updated".equalsIgnoreCase(sort)) {
            sortObj = Sort.by(Sort.Direction.DESC, "lastUpdated");
        } else {
            sortObj = Sort.by(Sort.Direction.DESC, "stars");
        }

        String filterLanguage = (language != null && !language.isBlank()) ? language.trim() : null;
        return githubRepo.findFilteredRepositories(filterLanguage, minStars, sortObj);
    }
}