package com.mohtashim.backend_assignment.service;

import com.mohtashim.backend_assignment.dto.Requestdto.SearchRequest;
import com.mohtashim.backend_assignment.dto.Responsedto.GithubResponse;
import com.mohtashim.backend_assignment.entity.GithubRepository;
import com.mohtashim.backend_assignment.exception.ApiException;
import com.mohtashim.backend_assignment.repository.GithubRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    @DisplayName("Successfully search GitHub API and upsert repositories in PostgreSQL")
    void testFetchAndSaveRepositories_Success() {
        GithubResponse.Owner owner = new GithubResponse.Owner();
        owner.setLogin("user123");

        GithubResponse.Item item = new GithubResponse.Item();
        item.setId(123456L);
        item.setName("spring-boot-example");
        item.setDescription("An example repository");
        item.setOwner(owner);
        item.setLanguage("Java");
        item.setStars(450);
        item.setForks(120);
        item.setLastUpdated(LocalDateTime.now());

        GithubResponse githubResponse = new GithubResponse();
        githubResponse.setItems(List.of(item));

        ResponseEntity<GithubResponse> responseEntity = new ResponseEntity<>(githubResponse, HttpStatus.OK);

        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(GithubResponse.class)))
                .thenReturn(responseEntity);

        when(githubRepo.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<GithubRepository> result = githubService.fetchAndSaveRepositories(searchRequest);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("spring-boot-example", result.get(0).getName());
        assertEquals("user123", result.get(0).getOwnerName());
        assertEquals(450, result.get(0).getStars());

        verify(githubRepo, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Throw 404 ApiException when GitHub returns empty search items")
    void testFetchAndSaveRepositories_NotFound() {
        GithubResponse githubResponse = new GithubResponse();
        githubResponse.setItems(List.of());

        ResponseEntity<GithubResponse> responseEntity = new ResponseEntity<>(githubResponse, HttpStatus.OK);

        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(GithubResponse.class)))
                .thenReturn(responseEntity);

        ApiException exception = assertThrows(ApiException.class, () -> {
            githubService.fetchAndSaveRepositories(searchRequest);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertTrue(exception.getMessage().contains("No repositories found on GitHub"));
    }

    @Test
    @DisplayName("Throw 429 ApiException when GitHub API rate limit is exceeded")
    void testFetchAndSaveRepositories_RateLimitExceeded() {
        when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(GithubResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.FORBIDDEN, "API rate limit exceeded"));

        ApiException exception = assertThrows(ApiException.class, () -> {
            githubService.fetchAndSaveRepositories(searchRequest);
        });

        assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatus());
        assertTrue(exception.getMessage().contains("rate limit exceeded"));
    }

    @Test
    @DisplayName("Throw 400 ApiException when query is blank")
    void testFetchAndSaveRepositories_BlankQuery() {
        searchRequest.setQuery("   ");

        ApiException exception = assertThrows(ApiException.class, () -> {
            githubService.fetchAndSaveRepositories(searchRequest);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    @DisplayName("Retrieve filtered and sorted repositories using database Sort")
    void testGetFilteredRepositories_SortByStars() {
        GithubRepository repo = new GithubRepository();
        repo.setId(1L);
        repo.setName("test-repo");
        repo.setLanguage("Java");
        repo.setStars(200);
        repo.setForks(50);
        repo.setLastUpdated(LocalDateTime.now());

        when(githubRepo.findFilteredRepositories(eq("Java"), eq(100), any(Sort.class)))
                .thenReturn(List.of(repo));

        List<GithubRepository> result = githubService.getFilteredRepositories("Java", 100, "stars");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("test-repo", result.get(0).getName());
        verify(githubRepo, times(1)).findFilteredRepositories(eq("Java"), eq(100), any(Sort.class));
    }

    @Test
    @DisplayName("Retrieve filtered repositories sorted by forks")
    void testGetFilteredRepositories_SortByForks() {
        when(githubRepo.findFilteredRepositories(isNull(), isNull(), any(Sort.class)))
                .thenReturn(List.of());

        List<GithubRepository> result = githubService.getFilteredRepositories(null, null, "forks");

        assertNotNull(result);
        verify(githubRepo, times(1)).findFilteredRepositories(isNull(), isNull(), any(Sort.class));
    }

    @Test
    @DisplayName("Retrieve filtered repositories sorted by updated")
    void testGetFilteredRepositories_SortByUpdated() {
        when(githubRepo.findFilteredRepositories(isNull(), isNull(), any(Sort.class)))
                .thenReturn(List.of());

        List<GithubRepository> result = githubService.getFilteredRepositories(null, null, "updated");

        assertNotNull(result);
        verify(githubRepo, times(1)).findFilteredRepositories(isNull(), isNull(), any(Sort.class));
    }
}