package com.example.movieapp.model.dto;

import com.example.movieapp.model.enums.FilmAccessType;
import com.example.movieapp.model.enums.FilmType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDto {
    Integer id;
    String title;
    String slug;
    String poster;
    FilmType type;
    FilmAccessType accessType;
    Double rating;
    Integer price;
    Boolean status;
    String trailerUrl;
}
