package com.example.movieapp.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogDetailDto {
    Integer id;
    String title;
    String slug;
    String description;
    String content;
    String thumbnail;
    Date createdAt;
    Date updatedAt;
    Date publishedAt;
    Boolean status;
    UserDto user;
}
