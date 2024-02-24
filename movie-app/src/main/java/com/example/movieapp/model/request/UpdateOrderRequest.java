package com.example.movieapp.model.request;

import com.example.movieapp.model.enums.OrderPaymentMethod;
import com.example.movieapp.model.enums.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class UpdateOrderRequest {
    @NotNull(message = "Phim id không được để trống")
    Integer filmId;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Giá tiền phải lớn hơn 0")
    Integer amount;

    @NotNull(message = "Trạng thái không được để trống")
    OrderStatus status;

    @NotNull(message = "Hình thức thanh toán không được để trống")
    OrderPaymentMethod paymentMethod;

    String note;
}
