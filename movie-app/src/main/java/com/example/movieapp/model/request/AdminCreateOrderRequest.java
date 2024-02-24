package com.example.movieapp.model.request;

import com.example.movieapp.model.enums.OrderPaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCreateOrderRequest {
    @NotNull(message = "User ID không được để trống")
    Integer userId;

    @NotNull(message = "Phim ID không được để trống")
    Integer filmId;

    @NotNull(message = "Hình thức thanh toán không được để trống")
    OrderPaymentMethod paymentMethod;
}
