package com.mori.mori.dto;

import lombok.Getter;

import java.util.Set;

public class CardResponseDTO {
    private long id;
    @Getter
    private String question;
    @Getter
    private String answer;
    @Getter
    private Set<String> tags; // 태그 이름만을 포함하는 String 타입의 Set

    public CardResponseDTO(){

    }
    public CardResponseDTO(long id, String question, String answer, Set<String> tags) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
