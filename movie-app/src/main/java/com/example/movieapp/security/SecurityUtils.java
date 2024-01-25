package com.example.movieapp.security;

import com.example.movieapp.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Slf4j
public class SecurityUtils {
    // Get the login of the current user.
    public static User getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Optional<User> userOptional = Optional.ofNullable(securityContext.getAuthentication()).map(authentication -> {
            if (authentication.getPrincipal() instanceof UserDetails) {
                CustomUserDetails springSecurityUser = (CustomUserDetails) authentication.getPrincipal();
                return springSecurityUser.getUser();
            }
            return null;
        });
        return userOptional.orElse(null);
    }

    // Check if a user is authenticated.
    public static boolean isAuthenticated() {
        User user = getCurrentUserLogin();
        return user != null;
    }
}
