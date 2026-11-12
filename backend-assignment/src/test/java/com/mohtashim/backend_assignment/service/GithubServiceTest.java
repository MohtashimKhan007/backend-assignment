package com.mohtashim.backend_assignment.service;

import com.mohtashim.backend_assignment.dto.Requestdto.SearchRequest;
import com.mohtashim.backend_assignment.dto.Responsedto.GithubResponse;
import com.mohtashim.backend_assignment.entity.GithubRepository;
import com.mohtashim.backend_assignment.exception.ApiException;
import com.mohtashim.backend_assignment.repository.GithubRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubServiceTest {

    @Mock
    private GithubRepo githubRepo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GithubService githubService;

    private SearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        searchRequest = new SearchRequest();
        searchRequest.setQuery("spring boot");
        searchRequest.setLanguage("Java");
        searchRequest.setSort("stars");
    }

    @Test
    void testFetchAndSaveRepositories_Success() {
        // Mocking GitHub API Response
        GithubResponse.Owner owner = new GithubResponse.Owner();
        owner.setLogin("user123");

        GithubResponse.Item item = new GithubResponse.Item();
        item.setId(123456L);
        item.setName("spring-boot-example");
        item.setDescription("Description");
        item.setOwner(owner);
        item.setLanguage("Java");
        item.setStars(450);
        item.setForks(120);
        item.setLastUpdated(LocalDateTime.now());

        GithubResponse githubResponse = new GithubResponse();
        githubResponse. setItems(List.of(item));

        ResponseEntity<GithubResponse> responseEntity = new ResponseEntity<>(githubResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(GithubResponse.class)))
                .thenReturn(responseEntity);

        when(githubRepo.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Execution
        List<GithubRepository> result = githubService.fetchAndSaveRepositories(searchRequest);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("spring-boot-example", result.get(0).getName());
        assertEquals(450, result.get(0).getStars());

        verify(githubRepo, times(1)).saveAll(any());
    }

    @Test
    void testFetchAndSaveRepositories_NotFound() {
        // Mocking empty response from GitHub API
        GithubResponse githubResponse = new GithubResponse();
        githubResponse.setItems(List.of());

        ResponseEntity<GithubResponse> responseEntity = new ResponseEntity<>(githubResponse, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(GithubResponse.class)))
                .thenReturn(responseEntity);

        // Verification of Custom Exception
        ApiException exception = assertThrows(ApiException.class, () -> {
            githubService.fetchAndSaveRepositories(searchRequest);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No repositories found on GitHub for the given criteria.", exception.getMessage());
    }

    @Test
    void testGetFilteredRepositories() {
        GithubRepository repo = new GithubRepository();
        repo.setId(1L);
        repo.setName("test-repo");
        repo.setLanguage("Java");
        repo.setStars(200);
        repo.setForks(50);
        repo.setLastUpdated(LocalDateTime.now());

        when(githubRepo.findFilteredRepositories("Java", 100))
                .thenReturn(List.of(repo));

        List<GithubRepository> result = githubService.getFilteredRepositories("Java", 100, "stars");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-repo", result.get(0).getName());
        verify(githubRepo, times(1)).findFilteredRepositories("Java", 100);
    }
}