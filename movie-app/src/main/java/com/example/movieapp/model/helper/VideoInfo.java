package com.example.movieapp.model.helper;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VideoInfo {
    Integer duration;
    String format;
    String resolution;
}
