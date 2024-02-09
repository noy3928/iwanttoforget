package com.soak.soak.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soak.soak.dto.elasticSearch.SearchRequestDTO;
import com.soak.soak.dto.elasticSearch.IndexRequestDTO;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElasticSearchService {

    private final RestHighLevelClient restHighLevelClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public ElasticSearchService(RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper) {
        this.restHighLevelClient = restHighLevelClient;
        this.objectMapper = objectMapper;
    }

    public <T> void indexDocument(IndexRequestDTO<T> indexRequestDTO) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexRequestDTO.getIndexName())
                .id(indexRequestDTO.getDocumentId())
                .source(objectMapper.writeValueAsString(indexRequestDTO.getDocument()), XContentType.JSON);
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    public List<?> searchDocuments(SearchRequestDTO searchRequestDTO) throws IOException {
        SearchRequest searchRequest = new SearchRequest(searchRequestDTO.getIndexName());
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        QueryBuilder queryBuilder;
        if(searchRequestDTO.getFields() != null && !searchRequestDTO.getFields().isEmpty()) {
            queryBuilder = QueryBuilders.multiMatchQuery(searchRequestDTO.getQuery(), searchRequestDTO.getFields().toArray(new String[0]));
        } else {
            queryBuilder = QueryBuilders.multiMatchQuery(searchRequestDTO.getQuery());
        }

        searchSourceBuilder.query(queryBuilder);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = searchResponse.getHits().getHits();

        return Arrays.stream(searchHits)
                .map(hit -> objectMapper.convertValue(hit.getSourceAsMap(), searchRequestDTO.getDomain()))
                .collect(Collectors.toList());
    }


}
