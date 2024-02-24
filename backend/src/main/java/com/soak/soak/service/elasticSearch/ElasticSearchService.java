package com.soak.soak.service.elasticSearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.soak.soak.controller.CardController;
import com.soak.soak.dto.elasticSearch.IndexRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService {

    private final ElasticsearchClient elasticsearchClient;

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);
    @Autowired
    public ElasticSearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public <T> void indexDocument(IndexRequestDTO<T> indexRequestDTO) throws IOException {
        IndexResponse response =  elasticsearchClient.index(i -> i
                .index(indexRequestDTO.getIndexName())
                .id(indexRequestDTO.getDocumentId())
                .document(indexRequestDTO.getDocument())
        );

        logger.info("Indexed with version " + response.version());
    }

    public <T> List<T> searchDocuments(
            String indexName,
            Class<T> clazz,
            Function<SearchRequest.Builder, SearchRequest.Builder> queryCustomizer
    ) throws IOException {
        SearchRequest searchRequest = queryCustomizer.apply(new SearchRequest.Builder().index(indexName)).build();
        SearchResponse<T> response = elasticsearchClient.search(searchRequest, clazz);

        return response.hits().hits().stream()
                .map(Hit::source)
                .collect(Collectors.toList());
    }

}


