package com.example.movieapp.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "watch_histories")
public class WatchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "film_id")
    Film film;

    @ManyToOne
    @JoinColumn(name = "episode_id")
    Episode episode;

    private Date watchTime;

    private Double duration;

    @PrePersist
    public void prePersist() {
        watchTime = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        watchTime = new Date();
    }
}
