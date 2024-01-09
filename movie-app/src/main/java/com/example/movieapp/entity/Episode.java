package com.example.movieapp.entity;

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
@Table(name = "episodes")
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "film_id")
    Film film;

    Integer displayOrder; // Tập 1, Tập 2, Tập 3, ...

    String title; // Tập 1: Tên tập phim
    Boolean status; // true: Đã phát sóng, false: Chưa phát sóng
    Date createdAt;
    Date updatedAt;
    Date publishedAt;

    @OneToOne
    @JoinColumn(name = "video_id")
    Video video;

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        updatedAt = createdAt;
        if (status) {
            publishedAt = createdAt;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
        if (status) {
            publishedAt = updatedAt;
        }
    }
}
