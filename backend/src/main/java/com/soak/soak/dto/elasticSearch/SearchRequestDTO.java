package com.soak.soak.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SearchRequestDTO {
    private String indexName;
    private String query;
    private Class<?> domain;
    private List<String> fields;

    public static SearchRequestDTO of(String indexName, String query, Class<?> domain, List<String> fields) {
        SearchRequestDTO dto = new SearchRequestDTO();
        dto.setIndexName(indexName);
        dto.setQuery(query);
        dto.setDomain(domain);
        dto.setFields(fields);
        return dto;
    }
}
