package com.example.movieapp.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePasswordRequest {
    @NotNull(message = "Mật khẩu cũ không được để trống")
    @NotEmpty(message = "Mật khẩu cũ không được để trống")
     String oldPassword;

    @NotNull(message = "Mật khẩu mới không được để trống")
    @NotEmpty(message = "Mật khẩu mới không được để trống")
     String newPassword;

    @NotNull(message = "Xác nhận mật khẩu không được để trống")
    @NotEmpty(message = "Xác nhận mật khẩu không được để trống")
     String confirmPassword;
}
