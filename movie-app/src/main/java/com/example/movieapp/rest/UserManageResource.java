package com.example.movieapp.rest;

import com.example.movieapp.model.request.UpdatePasswordRequest;
import com.example.movieapp.model.request.UpdateProfileUserRequest;
import com.example.movieapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManageResource {
    private final UserService userService;

    @PostMapping("/update-avatar")
    public ResponseEntity<?> updateAvatar(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.updateAvatar(file));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileUserRequest request) {
        userService.updateProfile(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(request);
        return ResponseEntity.noContent().build();
    }
}
