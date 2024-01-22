package com.soak.soak.controller;

import com.soak.soak.dto.CardDTO;
import com.soak.soak.dto.CardResponseDTO;
import com.soak.soak.repository.CardRepository;
import com.soak.soak.repository.CardTagMapRepository;
import com.soak.soak.repository.TagRepository;
import com.soak.soak.repository.UserRepository;
import com.soak.soak.service.CardService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable UUID id) {
        try {
            CardResponseDTO cardResponseDTO = cardService.getCardById(id);
            return new ResponseEntity<>(cardResponseDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    public ResponseEntity<CardResponseDTO> updateCard(@PathVariable UUID id, @RequestBody CardDTO cardDTO) {
        try {
            CardResponseDTO updatedCardResponseDTO = cardService.updateCard(id, cardDTO);
            return new ResponseEntity<>(updatedCardResponseDTO, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable UUID id) {
        try {
            cardService.deleteCard(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
