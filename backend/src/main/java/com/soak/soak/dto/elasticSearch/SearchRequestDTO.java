package com.soak.soak.dto.elasticSearch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDTO {
    private String indexName;
    private String query;
    private Class<?> domain;
    private List<String> fields;
    private Map<String, Object> filters;
}
