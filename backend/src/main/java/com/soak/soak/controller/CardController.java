package com.soak.soak.controller;

import com.soak.soak.dto.CardDTO;
import com.soak.soak.dto.CardResponseDTO;
import com.soak.soak.model.Card;
import com.soak.soak.model.CardTagMap;
import com.soak.soak.model.Tag;
import com.soak.soak.model.User;
import com.soak.soak.repository.CardRepository;
import com.soak.soak.repository.CardTagMapRepository;
import com.soak.soak.repository.TagRepository;
import com.soak.soak.repository.UserRepository;
import com.soak.soak.service.AuthService;
import com.soak.soak.service.CardService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.soak.soak.security.services.UserDetailsImpl;

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

    @Autowired
    UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private CardService cardService;

    @GetMapping("/cards")
    public ResponseEntity<List<CardResponseDTO>> getCards() {
        try {
            List<CardResponseDTO> cardResponseDTOs = cardService.getAllCards();
            return new ResponseEntity<>(cardResponseDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cards/{id}")
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable Long id) {
        try {
            Optional<Card> cardOptional = cardRepository.findById(id);

            if (cardOptional.isPresent()) {
                Card card = cardOptional.get();
                Set<String> tagNames = getTagNamesForCard(card);

                CardResponseDTO cardResponseDTO = new CardResponseDTO(
                        card.getId(),
                        card.getQuestion(),
                        card.getAnswer(),
                        tagNames,
                        card.isPublic()
                );

                return new ResponseEntity<>(cardResponseDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cards")
    public ResponseEntity<CardResponseDTO> createCard(@RequestBody CardDTO cardDTO) {
        try {
            CardResponseDTO cardResponseDTO = cardService.createCard(cardDTO);
            return new ResponseEntity<>(cardResponseDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/cards/{id}")
    @Transactional
    public ResponseEntity<CardResponseDTO> updateCard(@PathVariable Long id, @RequestBody CardDTO cardDTO) {
        try {
            Optional<Card> cardOptional = cardRepository.findById(id);
            System.out.println(cardOptional);

            if (cardOptional.isPresent()) {
                Card card = cardOptional.get();

                card.setQuestion(cardDTO.getQuestion());
                card.setAnswer(cardDTO.getAnswer());
                card.setPublic(cardDTO.isPublic());

                cardTagMapRepository.deleteByCard(card);
                Set<String> tagNames = new HashSet<>();
                for (String tagName : cardDTO.getTags()) {
                    Tag tag = tagRepository.findByName(tagName)
                            .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                    CardTagMap cardTagMap = new CardTagMap(card, tag);
                    cardTagMapRepository.save(cardTagMap);
                    tagNames.add(tag.getName());
                }

                Card updatedCard = cardRepository.save(card);

                CardResponseDTO cardResponseDTO = new CardResponseDTO(
                        updatedCard.getId(),
                        updatedCard.getQuestion(),
                        updatedCard.getAnswer(),
                        tagNames,
                        updatedCard.isPublic()
                );

                return new ResponseEntity<>(cardResponseDTO, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/cards/{id}")
    @Transactional
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        try {
            Optional<Card> cardOptional = cardRepository.findById(id);

            if (cardOptional.isPresent()) {
                Card card = cardOptional.get();

                // 카드에 연결된 태그 매핑 삭제
                cardTagMapRepository.deleteByCard(card);

                // 카드 삭제
                cardRepository.delete(card);

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private Set<String> getTagNamesForCard(Card card) {
        return cardTagMapRepository.findByCard(card).stream()
                .map(cardTagMap -> cardTagMap.getTag().getName())
                .collect(Collectors.toSet());
    }


}
