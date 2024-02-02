package com.example.movieapp.model.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewDto {
    Integer id;
    Integer rating;
    String comment;
    Date createdAt;
    Date updatedAt;
    UserDto user;
}
