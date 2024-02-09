package com.soak.soak.dto.card;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Getter
@Setter
@ToString
public class CardDTO {
    private String question;
    private String answer;
    private Set<String> tags; // 태그 이름을 문자열로 받음
    private boolean isPublic; // 카드의 공개 여부
}
