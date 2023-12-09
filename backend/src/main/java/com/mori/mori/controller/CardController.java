package com.mori.mori.controller;

import com.mori.mori.dto.CardDTO;
import com.mori.mori.dto.CardResponseDTO;
import com.mori.mori.model.Card;
import com.mori.mori.model.Tag;
import com.mori.mori.repository.CardRepository;
import com.mori.mori.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    CardRepository cardRepository;
    @Autowired
    TagRepository tagRepository;

    @PostMapping("/cards")
    public ResponseEntity<CardResponseDTO> createCard(@RequestBody CardDTO cardDTO){
        try {
            Set<Tag> tagEntities = convertTagNamesToTags(cardDTO.getTags());
            Card card = new Card(cardDTO.getQuestion(), cardDTO.getAnswer(), tagEntities);

            Card savedCard = cardRepository.save(card);
            CardResponseDTO cardResponseDTO = convertToCardResponseDTO(savedCard);
            return new ResponseEntity<>(cardResponseDTO, HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private CardResponseDTO convertToCardResponseDTO(Card card) {
        Set<String> tagNames = card.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return new CardResponseDTO(card.getId(), card.getQuestion(), card.getAnswer(), tagNames);
    }


    private Set<Tag> convertTagNamesToTags(Set<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElse(new Tag(tagName)); // Tag 객체 생성 또는 검색
            tags.add(tag);
        }
        return tags;
    }
}
