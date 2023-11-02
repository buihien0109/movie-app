package com.example.movieapp.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Film {
    private Integer id;
    private String title;
    private String description;
    private String poster;
    private Integer length;
    private Integer releaseYear;
    private String type;
    private Double rating;
    private Integer view;
}
