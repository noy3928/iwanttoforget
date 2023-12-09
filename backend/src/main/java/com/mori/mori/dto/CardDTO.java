package com.mori.mori.dto;

import java.util.Set;

public class CardDTO {
        private String question;
        private String answer;
        private Set<String> tags; // 태그 이름을 문자열로 받음

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
