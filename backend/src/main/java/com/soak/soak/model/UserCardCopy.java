package com.soak.soak.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_card_copy")
public class UserCardCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "card_id", referencedColumnName = "id")
    private Card card;

    @Column(name = "copied_at")
    private LocalDateTime copiedAt = LocalDateTime.now();
}
