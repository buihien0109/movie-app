package com.example.movieapp.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

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
    private String genre;
    private Double rating;
    private Integer view;
}
