package com.soak.soak.model;

import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "card_tag_map")
public class CardTagMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "tag_name", referencedColumnName = "name")
    private Tag tag;
}