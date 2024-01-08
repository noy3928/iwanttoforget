package com.soak.soak.controller;

import com.soak.soak.dto.CardDTO;
import com.soak.soak.dto.CardResponseDTO;
import com.soak.soak.model.Card;
import com.soak.soak.model.CardTagMap;
import com.soak.soak.model.Tag;
import com.soak.soak.repository.CardRepository;
import com.soak.soak.repository.CardTagMapRepository;
import com.soak.soak.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class CardController {

    @Autowired
    CardRepository cardRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    CardTagMapRepository cardTagMapRepository;

    @GetMapping("/cards")
    public ResponseEntity<List<CardResponseDTO>> getCards() {
        try {
            List<Card> cards = cardRepository.findAll();

            // 각 카드를 CardResponseDTO로 변환
            List<CardResponseDTO> cardResponseDTOs = cards.stream().map(card -> {
                Set<String> tagNames = cardTagMapRepository.findByCard(card).stream()
                        .map(cardTagMap -> cardTagMap.getTag().getName())
                        .collect(Collectors.toSet());

                return new CardResponseDTO(
                        card.getId(),
                        card.getQuestion(),
                        card.getAnswer(),
                        tagNames,
                        card.isPublic()
                );
            }).collect(Collectors.toList());

            return new ResponseEntity<>(cardResponseDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/cards")
    public ResponseEntity<CardResponseDTO> createCard(@RequestBody CardDTO cardDTO) {
        try {
            // Card 엔티티 생성 및 저장
            Card card = new Card();
            card.setQuestion(cardDTO.getQuestion());
            card.setAnswer(cardDTO.getAnswer());
            card.setPublic(cardDTO.isPublic());
            Card savedCard = cardRepository.save(card);

            Set<String> tagNames = new HashSet<>();

            // 각 태그에 대해 CardTagMap 엔티티 생성 및 저장
            for (String tagName : cardDTO.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                CardTagMap cardTagMap = new CardTagMap(savedCard, tag);
                cardTagMapRepository.save(cardTagMap);

                tagNames.add(tag.getName());
            }

            // CardResponseDTO 변환
            CardResponseDTO cardResponseDTO = new CardResponseDTO(
                    savedCard.getId(),
                    savedCard.getQuestion(),
                    savedCard.getAnswer(),
                    tagNames,
                    savedCard.isPublic()
            );

            return new ResponseEntity<>(cardResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
