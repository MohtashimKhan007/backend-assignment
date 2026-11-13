package com.mohtashim.backend_assignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohtashim.backend_assignment.dto.Requestdto.SearchRequest;
import com.mohtashim.backend_assignment.entity.GithubRepository;
import com.mohtashim.backend_assignment.exception.GlobalExceptionHandler;
import com.mohtashim.backend_assignment.service.GithubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GithubControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private GithubService githubService;

    @InjectMocks
    private GithubController githubController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(githubController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/github/search - Returns 200 OK with repository list")
    void testSearchEndpoint_Success() throws Exception {
        SearchRequest request = new SearchRequest("spring boot", "Java", "stars");

        GithubRepository repo = new GithubRepository(
                123L, "spring-boot-example", "Example repo", "user123", "Java", 450, 120, LocalDateTime.now()
        );

        when(githubService.fetchAndSaveRepositories(any(SearchRequest.class)))
                .thenReturn(List.of(repo));

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Repositories fetched and saved successfully"))
                .andExpect(jsonPath("$.repositories[0].name").value("spring-boot-example"))
                .andExpect(jsonPath("$.repositories[0].owner").value("user123"));
    }

    @Test
    @DisplayName("GET /api/github/repositories - Returns 200 OK with filtered list")
    void testGetRepositoriesEndpoint_Success() throws Exception {
        GithubRepository repo = new GithubRepository(
                123L, "spring-boot-example", "Example repo", "user123", "Java", 450, 120, LocalDateTime.now()
        );

        when(githubService.getFilteredRepositories("Java", 100, "stars"))
                .thenReturn(List.of(repo));

        mockMvc.perform(get("/api/github/repositories")
                        .param("language", "Java")
                        .param("minStars", "100")
                        .param("sort", "stars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositories[0].name").value("spring-boot-example"))
                .andExpect(jsonPath("$.repositories[0].stars").value(450));
    }
}
