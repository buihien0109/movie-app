package com.example.movieapp.entity;

import com.example.movieapp.model.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    String phone;

    @JsonIgnore
    @Column(nullable = false)
    String password;

    String avatar;

    @Enumerated(EnumType.STRING)
    UserRole role;

    private Boolean enabled;

    Date createdAt;
    Date updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        updatedAt = createdAt;

        if (role == null) {
            role = UserRole.USER;
        }

        if (enabled == null) {
            enabled = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
    }
}
