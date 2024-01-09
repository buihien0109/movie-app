package com.example.movieapp.model.request;

import com.example.movieapp.model.enums.FilmType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFilmRequest {
    @NotEmpty(message = "Tên phim không được để trống")
    String title;

    String description;

    @NotNull(message = "Năm phát hành không được để trống")
    Integer releaseYear;

    @NotNull(message = "Thể loại không được để trống")
    FilmType type;

    @NotNull(message = "Trạng thái không được để trống")
    Boolean status;

    Set<Integer> genreIds;
    Set<Integer> directorIds;
    Set<Integer> actorIds;
}
