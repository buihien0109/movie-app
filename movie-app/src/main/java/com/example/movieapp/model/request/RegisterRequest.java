package com.example.movieapp.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @NotEmpty(message = "Tên không được để trống")
    String name;

    @NotEmpty(message = "Tên đăng nhập không được để trống")
    @Email(message = "Email không đúng định dạng")
    String email;

    @NotEmpty(message = "Mật khẩu không được để trống")
    String password;

    @NotEmpty(message = "Xác nhận mật khẩu không được để trống")
    String confirmPassword;
}
