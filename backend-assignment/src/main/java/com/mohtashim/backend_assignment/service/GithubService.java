package com.mohtashim.backend_assignment.service;

import com.mohtashim.backend_assignment.dto.Requestdto.SearchRequest;
import com.mohtashim.backend_assignment.dto.Responsedto.GithubResponse;
import com.mohtashim.backend_assignment.entity.GithubRepository;
import com.mohtashim.backend_assignment.exception.ApiException;
import com.mohtashim.backend_assignment.repository.GithubRepo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GithubService {

    private final GithubRepo githubRepo;
    private final RestTemplate restTemplate;

    public GithubService(GithubRepo githubRepo, RestTemplate restTemplate) {
        this.githubRepo = githubRepo;
        this.restTemplate = restTemplate;
    }

    public List<GithubRepository> fetchAndSaveRepositories(SearchRequest request) {
        // Query aur language ko combine karne ka clean tarika
        String searchQuery = request.getQuery() != null ? request.getQuery().trim() : "";

        if (request.getLanguage() != null && !request.getLanguage().isBlank()) {
            searchQuery = searchQuery + " language:" + request.getLanguage().trim().toLowerCase();
        }

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("https://api.github.com/search/repositories")
                .queryParam("q", searchQuery);

        if (request.getSort() != null && !request.getSort().isBlank()) {
            uriBuilder.queryParam("sort", request.getSort().trim())
                    .queryParam("order", "desc");
        }

        try {
            ResponseEntity<GithubResponse> responseEntity = restTemplate.getForEntity(uriBuilder.toUriString(), GithubResponse.class);
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
                repoEntity.setStars(item.getStars());
                repoEntity.setForks(item.getForks());
                repoEntity.setLastUpdated(item.getLastUpdated());

                entities.add(repoEntity);
            }

            return githubRepo.saveAll(entities);

        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Failed to communicate with GitHub API: " + e.getMessage());
        }
    }

    public List<GithubRepository> getFilteredRepositories(String language, Integer minStars, String sort) {
        List<GithubRepository> filteredRepos = githubRepo.findFilteredRepositories(language, minStars);

        return filteredRepos.stream()
                .sorted((r1, r2) -> {
                    if ("forks".equalsIgnoreCase(sort)) {
                        return Integer.compare(
                                r2.getForks() == null ? 0 : r2.getForks(),
                                r1.getForks() == null ? 0 : r1.getForks()
                        );
                    } else if ("updated".equalsIgnoreCase(sort)) {
                        if (r2.getLastUpdated() == null) return 1;
                        if (r1.getLastUpdated() == null) return -1;
                        return r2.getLastUpdated().compareTo(r1.getLastUpdated());
                    } else {
                        return Integer.compare(
                                r2.getStars() == null ? 0 : r2.getStars(),
                                r1.getStars() == null ? 0 : r1.getStars()
                        );
                    }
                })
                .collect(Collectors.toList());
    }
}