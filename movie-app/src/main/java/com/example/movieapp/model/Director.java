package com.example.movieapp.model;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Director {
    private Integer id;
    private String name;
    private String avatar;
    private LocalDate birthDate;
}
