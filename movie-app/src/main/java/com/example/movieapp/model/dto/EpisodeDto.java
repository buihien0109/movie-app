package com.example.movieapp.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EpisodeDto {
    Integer id;
    Integer displayOrder; // Tập 1, Tập 2, Tập 3, ...
    String title; // Tập 1: Tên tập phim
    Boolean status; // true: Đã phát sóng, false: Chưa phát sóng
    Integer duration;
    String videoUrl;
}
