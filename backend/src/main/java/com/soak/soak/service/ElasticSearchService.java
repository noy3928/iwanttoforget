package com.soak.soak.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.soak.soak.dto.elasticSearch.SearchRequestDTO;
import com.soak.soak.dto.elasticSearch.IndexRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService {

    private final ElasticsearchClient elasticsearchClient;
    @Autowired
    public ElasticSearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public <T> void indexDocument(IndexRequestDTO<T> indexRequestDTO) throws IOException {
         elasticsearchClient.index(i -> i
                .index(indexRequestDTO.getIndexName())
                .id(indexRequestDTO.getDocumentId())
                .document(indexRequestDTO.getDocument()));
    }

    public <T> List<T> searchDocuments(SearchRequestDTO searchRequestDTO, Class<T> clazz) throws IOException {
        SearchResponse<T> searchResponse = elasticsearchClient.search(s -> s
                        .index(searchRequestDTO.getIndexName())
                        .query(q -> q
                                .multiMatch(t -> t
                                        .query(searchRequestDTO.getQuery())
                                        .fields(searchRequestDTO.getFields()))),
                clazz);

        return searchResponse.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }
}

