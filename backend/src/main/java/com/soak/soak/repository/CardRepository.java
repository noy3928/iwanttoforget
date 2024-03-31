package com.soak.soak.repository;

import com.soak.soak.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    Page<Card> findByUserIdAndIsPublic(UUID userId, boolean isPublic, Pageable pageable);
    Page<Card> findByUserId(UUID userId, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Card c " +
            "LEFT JOIN CardTagMap ctm ON c.id = ctm.card.id " +
            "LEFT JOIN Tag t ON ctm.tag.name = t.name " +
            "WHERE LOWER(c.question) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.answer) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Card> searchCards(@Param("query") String query);

    @Query("SELECT c FROM Card c WHERE c.id IN :cardIds AND c.user.id = :userId")
    Page<Card> findAllByIdAndUserId(@Param("cardIds") Set<UUID> cardIds, @Param("userId") UUID userId, Pageable pageable);

    Page<Card> findAllByIdInAndUserIdAndIsPublic(Set<UUID> cardIds, UUID userId, boolean isPublic, Pageable pageable);
}
