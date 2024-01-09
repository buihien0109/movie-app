package com.example.movieapp.model.enums;

import lombok.Getter;

@Getter
public enum FilmType {
    PHIM_CHIEU_RAP("Phim chiếu rạp"),
    PHIM_LE("Phim lẻ"),
    PHIM_BO("Phim bộ");

    private final String type;

    FilmType(String type) {
        this.type = type;
    }
}
