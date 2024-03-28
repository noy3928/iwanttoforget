package com.soak.soak.dto.card;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CardResponseDTO {
    private UUID id;
    private String question;
    private String answer;
    private Set<String> tags; // 태그 이름만을 포함하는 String 타입의 Set
    private boolean isPublic; // 카드의 공개 여부

    public CardResponseDTO() {
    }

    public CardResponseDTO(UUID id, String question, String answer, Set<String> tags, boolean isPublic) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.tags = tags;
        this.isPublic = isPublic;
    }
}
