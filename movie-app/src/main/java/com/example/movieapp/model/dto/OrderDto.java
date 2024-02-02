package com.example.movieapp.model.dto;

import com.example.movieapp.model.enums.OrderPaymentMethod;
import com.example.movieapp.model.enums.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDto {
    Integer id;
    Integer amount;
    OrderStatus status;
    OrderPaymentMethod paymentMethod;
    Date createdAt;
    FilmDto film;
}
