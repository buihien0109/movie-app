package com.example.movieapp.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpsertEpisodeRequest {
    @NotNull(message = "Thứ tự tập phim không được để trống")
    Integer displayOrder; // Tập 1, Tập 2, Tập 3, ...

    @NotEmpty(message = "Tiêu đề tập phim không được để trống")
    String title; // Tập 1: Tên tập phim

    @NotNull(message = "ID phim không được để trống")
    Integer filmId;

    @NotNull(message = "Trạng thái tập phim không được để trống")
    Boolean status;
}
