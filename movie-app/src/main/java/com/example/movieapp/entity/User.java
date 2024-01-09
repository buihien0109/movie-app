package com.example.movieapp.entity;

import com.example.movieapp.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String name;

    @Column(unique = true)
    String email;

    String password;

    String avatar;

    @Enumerated(EnumType.STRING)
    UserRole role;

    Date createdAt;
    Date updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
    }
}
