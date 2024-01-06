package com.soak.soak.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CardDTO {
    private String question;
    private String answer;
    private Set<String> tags; // 태그 이름을 문자열로 받음
    private boolean isPublic; // 카드의 공개 여부
}
