package com.example.movieapp.model.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateDirectorRequest {
    @NotEmpty(message = "Tên đạo diễn không được để trống")
    String name;

    @NotEmpty(message = "Mô tả không được để trống")
    String description;

    @NotNull(message = "Ngày sinh không được để trống")
    Date birthday;
}

