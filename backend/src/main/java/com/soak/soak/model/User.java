package com.soak.soak.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @NotBlank
    @Size(max = 20)
    private String username;

    @Getter
    @Setter
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @Getter
    @Setter
    @NotBlank
    @Size(max = 120)
    private String password;

    @Getter
    @Setter
    @Size(max = 255)  // 설명의 최대 길이를 지정합니다.
    private String description;  // 필드 이름은 소문자로 시작해야 합니다.

    @Getter
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 계정 생성 시각

    @Getter
    @Column(name = "last_login")
    private LocalDateTime lastLogin;  // 마지막 로그인 시각
}
