package com.example.movieapp.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogDto {
    Integer id;
    String title;
    String slug;
    String description;
    String thumbnail;
    Date publishedAt;
}
