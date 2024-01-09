package com.example.movieapp.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpsertBlogRequest {
    String title;
    String description;
    String content;
    Boolean status;
}
