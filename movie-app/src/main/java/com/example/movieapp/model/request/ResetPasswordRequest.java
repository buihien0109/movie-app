package com.example.movieapp.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordRequest {
    @NotNull(message = "Token không được để trống")
    @NotEmpty(message = "Token không được để trống")
    String token;

    @NotNull(message = "Mật khẩu mới không được để trống")
    @NotEmpty(message = "Mật khẩu mới không được để trống")
    String newPassword;

    @NotNull(message = "Mật khẩu xác nhận không được để trống")
    @NotEmpty(message = "Mật khẩu xác nhận không được để trống")
    String confirmPassword;
}
