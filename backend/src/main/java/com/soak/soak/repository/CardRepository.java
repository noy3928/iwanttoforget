package com.soak.soak.repository;

import com.soak.soak.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CardRepository extends JpaRepository<Card, UUID> {
    List<Card> findByUserIdAndIsPublic(UUID userId, boolean isPublic);
}
