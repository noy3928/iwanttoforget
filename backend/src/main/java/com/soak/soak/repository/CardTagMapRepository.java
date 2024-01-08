package com.soak.soak.repository;

import com.soak.soak.model.Card;
import com.soak.soak.model.CardTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardTagMapRepository extends JpaRepository<CardTagMap, Long> {
    List<CardTagMap> findByCard(Card card);
}
