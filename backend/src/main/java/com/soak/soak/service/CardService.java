package com.soak.soak.service;

import com.soak.soak.dto.card.CardDTO;
import com.soak.soak.dto.card.CardResponseDTO;
import com.soak.soak.dto.elasticSearch.IndexRequestDTO;
import com.soak.soak.dto.elasticSearch.SearchRequestDTO;
import com.soak.soak.model.*;
import com.soak.soak.repository.*;
import com.soak.soak.security.services.UserDetailsImpl;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;

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
    private UserCardCopyRepository userCardCopyRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    public List<CardResponseDTO> getAllCards() {
        UserDetailsImpl currentUser = authService.getCurrentAuthenticatedUserDetails();
        UUID userId = currentUser.getId();

        List<Card> cards = cardRepository.findByUserId(userId);

        return cards.stream().map(this::convertCardToCardResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public CardResponseDTO createCard(CardDTO cardDTO) {
        Card card = createAndSaveCard(cardDTO);
        indexCardInElasticsearch(card);
        return convertCardToCardResponseDTO(card);
    }

    @Transactional
    public CardResponseDTO updateCard(UUID id, CardDTO cardDTO) {
        UserDetailsImpl currentUser = authService.getCurrentAuthenticatedUserDetails();
        User user = userRepository.findById(currentUser.getId()).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );

        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with id: " + id));

        card.setQuestion(cardDTO.getQuestion());
        card.setAnswer(cardDTO.getAnswer());
        card.setPublic(cardDTO.isPublic());
        card.setUser(user);

        card = cardRepository.save(card);
        createOrUpdateCardTags(card, cardDTO.getTags());

        return convertCardToCardResponseDTO(card);
    }


    @Transactional
    public void deleteCard(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with id: " + id));
        cardTagMapRepository.deleteByCard(card);
        cardRepository.delete(card);
    }


    @Transactional
    public CardResponseDTO copyCard(UUID cardId) {
        User user = getUserEntity();
        Card originalCard = getPublicCardById(cardId);

        validateCardCopy(originalCard, user);

        Card copiedCard = copyCardDetails(originalCard, user);
        copiedCard = cardRepository.save(copiedCard);

        copyCardTags(originalCard, copiedCard);
        recordCardCopy(originalCard, user);

        return convertCardToCardResponseDTO(copiedCard);
    }

    public List<CardResponseDTO> searchCards(String query) {
        List<CardResponseDTO> searchResults = new ArrayList<>();

        try {
            SearchRequestDTO searchRequestDTO = SearchRequestDTO.of("cards", query, Card.class, Arrays.asList("question", "answer", "tags"));
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                    .must(QueryBuilders.multiMatchQuery(query, searchRequestDTO.getFields().toArray(new String[0])))
                    .filter(QueryBuilders.termQuery("isPublic", true));

            List<Card> cards = (List<Card>) elasticSearchService.searchDocuments(searchRequestDTO, boolQueryBuilder);
            searchResults = cards.stream().map(this::convertCardToCardResponseDTO).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Failed to search cards in Elasticsearch", e);
        }

        return searchResults;
    }


    private Card createAndSaveCard(CardDTO cardDTO) {
        UserDetailsImpl currentUser = getCurrentAuthenticatedUserDetails();
        User user = getUserById(currentUser.getId());

        Card card = new Card();
        card.setQuestion(cardDTO.getQuestion());
        card.setAnswer(cardDTO.getAnswer());
        card.setPublic(cardDTO.isPublic());
        card.setUser(user);
        card = cardRepository.save(card);
        createOrUpdateCardTags(card, cardDTO.getTags());

        return card;
    }

    private void indexCardInElasticsearch(Card card) {
        try {
            IndexRequestDTO<Card> indexRequestDTO = IndexRequestDTO.of("cards", card.getId().toString(), card);
            elasticSearchService.indexDocument(indexRequestDTO);
        } catch (IOException e) {
            logger.error("Failed to index card in Elasticsearch", e);
            // Optional: Handle this error appropriately.
        }
    }

    private UserDetailsImpl getCurrentAuthenticatedUserDetails() {
        return authService.getCurrentAuthenticatedUserDetails();
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
    }


    private UserDetailsImpl getCurrentUser() {
        return authService.getCurrentAuthenticatedUserDetails();
    }

    private User getUserEntity() {
        UserDetailsImpl currentUserDetails = getCurrentUser();
        return userRepository.findById(currentUserDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private Card getPublicCardById(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + cardId));
        if (!card.isPublic()) {
            throw new IllegalArgumentException("Cannot copy a private card");
        }
        return card;
    }

    private void validateCardCopy(Card card, User currentUser) {
        if (card.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Cannot copy own card");
        }
    }

    private Card copyCardDetails(Card originalCard, User currentUser) {
        Card copiedCard = new Card();
        copiedCard.setQuestion(originalCard.getQuestion());
        copiedCard.setAnswer(originalCard.getAnswer());
        copiedCard.setPublic(false);
        copiedCard.setUser(currentUser);
        return copiedCard;
    }

    private void copyCardTags(Card originalCard, Card copiedCard) {
        createOrUpdateCardTags(copiedCard, getTagNamesForCard(originalCard));
    }

    private void recordCardCopy(Card originalCard, User currentUser){
        UserCardCopy userCardCopy = new UserCardCopy();
        userCardCopy.setUser(currentUser);
        userCardCopy.setCard(originalCard);
        userCardCopyRepository.save(userCardCopy);
    }


    // CardService 클래스 내의 수정된 getCardById 메서드
    public CardResponseDTO getCardById(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Card not found with id: " + id));
        return convertCardToCardResponseDTO(card);
    }




    public List<CardResponseDTO> getCardsByUserId(UUID userId) {
        List<Card> cards = cardRepository.findByUserIdAndIsPublic(userId, true);
        return cards.stream().map(this::convertCardToCardResponseDTO).collect(Collectors.toList());
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

    private CardResponseDTO convertCardToCardResponseDTO(Card card) {
        Set<String> tagNames = getTagNamesForCard(card);
        return new CardResponseDTO(
                card.getId(),
                card.getQuestion(),
                card.getAnswer(),
                tagNames,
                card.isPublic()
        );
    }

    private Set<String> getTagNamesForCard(Card card) {
        return cardTagMapRepository.findByCard(card).stream()
                .map(cardTagMap -> cardTagMap.getTag().getName())
                .collect(Collectors.toSet());
    }
}

