package com.example.movieapp.entity;

import com.example.movieapp.model.enums.FilmType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "films")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    String title;
    String slug;

    @Column(columnDefinition = "TEXT")
    String description;

    String poster;
    Integer releaseYear;
    Integer view;
    Double rating;

    @Enumerated(EnumType.STRING)
    FilmType type; // Loại phim: Phim lẻ, Phim bộ, Phim chiếu rạp

    Boolean status; // Nháp: false, Công khai: true
    Date createdAt;
    Date updatedAt;
    Date publishedAt;

    @ManyToMany
    @JoinTable(name = "film_genre",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @Fetch(FetchMode.SUBSELECT)
    private Set<Genre> genres = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "film_director",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id"))
    @Fetch(FetchMode.SUBSELECT)
    private Set<Director> directors = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "film_actor",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id"))
    @Fetch(FetchMode.SUBSELECT)
    private Set<Actor> actors = new LinkedHashSet<>();

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
