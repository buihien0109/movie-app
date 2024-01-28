package com.example.movieapp.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WatchHistoryRequest {
    @NotNull(message = "FilmId không được để trống")
    Integer filmId;

    @NotNull(message = "EpisodeId không được để trống")
    Integer episodeId;

    @NotNull(message = "Duration không được để trống")
    Double duration;
}
