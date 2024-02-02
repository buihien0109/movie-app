package com.example.movieapp.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderStatus {
    PENDING("Chờ xử lý"),
    SUCCESS("Thành công"),
    CANCEL("Đã hủy");

    private final String value;
}
