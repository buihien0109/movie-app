package com.example.movieapp.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpsertGenreRequest {
    @NotEmpty(message = "Tên thể loại không được để trống")
    String name;
}
