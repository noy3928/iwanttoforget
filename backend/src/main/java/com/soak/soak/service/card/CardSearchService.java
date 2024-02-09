package com.soak.soak.service.card;

import com.soak.soak.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardSearchService {
    @Autowired
    private ElasticSearchService elasticSearchService;
}
