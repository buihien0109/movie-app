package com.example.movieapp.model.dto;

import com.example.movieapp.model.enums.UserRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Integer id;
    String name;
    String email;
    String phone;
    String avatar;
    UserRole role;
    Boolean enabled;
}
