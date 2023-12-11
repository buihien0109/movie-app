package com.example.movieapp.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Genre {
    private Integer id;
    private String name;
}
