package com.soak.soak.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndexRequestDTO<T> {
    private String indexName;
    private String documentId;
    private T document;
    public static <T> IndexRequestDTO<T> of(String indexName, String documentId, T document) {
        IndexRequestDTO<T> dto = new IndexRequestDTO<>();
        dto.setIndexName(indexName);
        dto.setDocumentId(documentId);
        dto.setDocument(document);
        return dto;
    }
}

