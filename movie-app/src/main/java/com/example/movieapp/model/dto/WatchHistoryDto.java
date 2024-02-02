package com.example.movieapp.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchHistoryDto {
    Integer id;
    Date watchTime;
    Double duration;
    UserDto user;
    FilmDto film;
    EpisodeDto episode;
}
