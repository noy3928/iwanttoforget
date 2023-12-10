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

    @GetMapping("/cards")
    public ResponseEntity<List<CardResponseDTO>> getAllCards() {
        try {
            List<Card> allCards = cardRepository.findAll();
            List<CardResponseDTO> cardResponseDTOs = allCards.stream()
                    .map(this::convertToCardResponseDTO)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(cardResponseDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable Long id) {
        try {
            Optional<Card> cardData = cardRepository.findById(id);

            if (cardData.isPresent()) {
                Card card = cardData.get();
                CardResponseDTO cardResponseDTO = convertToCardResponseDTO(card);
                return new ResponseEntity<>(cardResponseDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @PutMapping("/cards/{id}")
    public ResponseEntity<CardResponseDTO> updateCard(@PathVariable Long id, @RequestBody CardDTO cardDTO) {
        try {
            Optional<Card> cardData = cardRepository.findById(id);

            if (cardData.isPresent()) {
                Card existingCard = cardData.get();

                existingCard.setQuestion(cardDTO.getQuestion());
                existingCard.setAnswer(cardDTO.getAnswer());
                Set<Tag> tagEntities = convertTagNamesToTags(cardDTO.getTags());
                existingCard.setTags(tagEntities);

                Card updatedCard = cardRepository.save(existingCard);

                CardResponseDTO cardResponseDTO = convertToCardResponseDTO(updatedCard);
                return new ResponseEntity<>(cardResponseDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private CardResponseDTO convertToCardResponseDTO(Card card) {
        Set<String> tagNames = card.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        CardResponseDTO cardResponseDTO = new CardResponseDTO();
        cardResponseDTO.setId(card.getId()); // id 설정
        cardResponseDTO.setQuestion(card.getQuestion());
        cardResponseDTO.setAnswer(card.getAnswer());
        cardResponseDTO.setTags(tagNames);

        return cardResponseDTO;
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
