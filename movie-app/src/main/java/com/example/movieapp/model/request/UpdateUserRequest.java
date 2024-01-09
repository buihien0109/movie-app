package com.example.movieapp.model.request;

import com.example.movieapp.model.enums.UserRole;
import jakarta.validation.constraints.Email;
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
public class UpdateUserRequest {
    @NotEmpty(message = "Tên không được để trống")
    String name;

    @NotNull(message = "Role không được để trống")
    UserRole role;
}
