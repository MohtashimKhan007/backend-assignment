package com.mohtashim.backend_assignment.dto.Requestdto;

import lombok.Data;

@Data
public class SearchRequest {
    private String query;
    private String language;
    private String sort;
}