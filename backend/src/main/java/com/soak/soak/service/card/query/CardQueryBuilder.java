package com.soak.soak.service.card.query;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import java.util.Arrays;
import java.util.function.Function;

public class CardQueryBuilder {

    /**
     * Creates a query customizer for card search queries.
     *
     * @param query The search query string.
     * @param isPublicFilter Whether to filter by public cards.
     * @return A function that customizes a SearchRequest.Builder with the given query and filter.
     */
    public static Function<SearchRequest.Builder, SearchRequest.Builder> buildCardQuery(String query, boolean isPublicFilter) {
        return builder -> builder.query(q -> q
                .bool(b -> {
                    b.must(m -> m
                            .multiMatch(mm -> mm
                                    .query(query)
                                    .fields(Arrays.asList("question", "answer", "tags"))
                            )
                    );
                    if (isPublicFilter) {
                        b.filter(f -> f
                                .term(t -> t
                                        .field("isPublic")
                                        .value(isPublicFilter)
                                )
                        );
                    }
                    return b;
                })
        );
    }
}
