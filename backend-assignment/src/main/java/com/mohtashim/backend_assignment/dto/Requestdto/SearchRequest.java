package com.mohtashim.backend_assignment.dto.Requestdto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request criteria for searching repositories on GitHub")
public class SearchRequest {

    @NotBlank(message = "Search query is required and cannot be empty")
    @Schema(description = "Repository name or keyword to search (partial or full match)", example = "spring boot", requiredMode = Schema.RequiredMode.REQUIRED)
    private String query;

    @Schema(description = "Programming language filter", example = "Java")
    private String language;

    @Schema(description = "Sort criteria: 'stars', 'forks', or 'updated'", example = "stars", allowableValues = {"stars", "forks", "updated"})
    private String sort;
}