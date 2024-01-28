package com.example.movieapp.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FilmAccessType {
    FREE("Miễn phí"),
    PAID("Trả phí");

    private final String value;
}
