package com.soak.soak.service;

import com.soak.soak.dto.CardDTO;
import com.soak.soak.model.Card;
import com.soak.soak.model.CardTagMap;
import com.soak.soak.model.Tag;
import com.soak.soak.model.User;
import com.soak.soak.repository.CardRepository;
import com.soak.soak.repository.CardTagMapRepository;
import com.soak.soak.repository.TagRepository;
import com.soak.soak.repository.UserRepository;
import com.soak.soak.security.services.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardTagMapRepository cardTagMapRepository;

    @Autowired
    private AuthService authService;

    @Transactional
    public Card createCard(CardDTO cardDTO) {
        UserDetailsImpl currentUser = authService.getCurrentAuthenticatedUserDetails();
        User user = userRepository.findById(currentUser.getId()).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        Card card = new Card();
        card.setQuestion(cardDTO.getQuestion());
        card.setAnswer(cardDTO.getAnswer());
        card.setPublic(cardDTO.isPublic());
        card.setUser(user);
        card = cardRepository.save(card);

        createOrUpdateCardTags(card, cardDTO.getTags());

        return card;
    }

    @Transactional
    public Card updateCard(Long id, CardDTO cardDTO) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with id: " + id));

        card.setQuestion(cardDTO.getQuestion());
        card.setAnswer(cardDTO.getAnswer());
        card.setPublic(cardDTO.isPublic());
        card = cardRepository.save(card);

        createOrUpdateCardTags(card, cardDTO.getTags());

        return card;
    }

    @Transactional
    public void deleteCard(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with id: " + id));
        cardTagMapRepository.deleteByCard(card);
        cardRepository.delete(card);
    }

    public Optional<Card> getCardById(Long id) {
        return cardRepository.findById(id);
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    private void createOrUpdateCardTags(Card card, Set<String> tagNames) {
        // Remove existing tags if any
        cardTagMapRepository.deleteByCard(card);

        // Process new tags
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> tagRepository.save(new Tag(tagName)));

            CardTagMap cardTagMap = new CardTagMap(card, tag);
            cardTagMapRepository.save(cardTagMap);
        }
    }
}
