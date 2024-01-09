package com.example.movieapp.service;

import com.example.movieapp.entity.User;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.exception.ResouceNotFoundException;
import com.example.movieapp.model.enums.UserRole;
import com.example.movieapp.model.request.LoginRequest;
import com.example.movieapp.model.request.RegisterRequest;
import com.example.movieapp.repository.UserRepository;
import com.example.movieapp.utils.StringUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final HttpSession session;


    public void login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResouceNotFoundException("Không tìm thấy tài khoản với email: " + request.getEmail()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu không chính xác");
        }

        log.info("User logged in: {}", user);
        session.setAttribute("currentUser", user);
    }

    public void register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu không khớp");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setAvatar(StringUtils.generateLinkImage(request.getName()));

        userRepository.save(user);
        log.info("New user registered: {}", user);
    }

    public void logout() {
        log.info("Logout request");
        log.info("User logged out: {}", session.getAttribute("currentUser"));
        session.removeAttribute("currentUser");
    }
}
