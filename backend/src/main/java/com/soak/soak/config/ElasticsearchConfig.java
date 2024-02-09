package com.soak.soak.config;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

@Configuration
public class ElasticsearchConfig {
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // Elasticsearch 호스트와 포트를 지정합니다.
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("localhost", 9200, "http"));

        return new RestHighLevelClient(builder);
    }
}
