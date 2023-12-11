package com.example.movieapp.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Actor {
    private Integer id;
    private String name;
    private String description;
    private String avatar;
    private LocalDate birthDate;
}
