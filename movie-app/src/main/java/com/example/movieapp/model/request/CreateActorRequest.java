package com.example.movieapp.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateActorRequest {
    String name;
    String description;
    Date birthday;
}
