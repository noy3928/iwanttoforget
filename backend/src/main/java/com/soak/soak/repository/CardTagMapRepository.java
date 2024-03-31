package com.soak.soak.repository;

import com.soak.soak.model.Card;
import com.soak.soak.model.CardTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CardTagMapRepository extends JpaRepository<CardTagMap, Long> {
    List<CardTagMap> findByCard(Card card);
    void deleteByCard(Card card);

    @Query("SELECT ctm.card.id FROM CardTagMap ctm WHERE LOWER(ctm.tag.name) = LOWER(:tag)")
    Set<UUID> findCardIdsByTagName(@Param("tag") String tag);
}
