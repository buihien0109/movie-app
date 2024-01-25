package com.example.movieapp.service;

import com.example.movieapp.constant.Constant;
import com.example.movieapp.entity.User;
import com.example.movieapp.exception.BadRequestException;
import com.example.movieapp.model.enums.UserRole;
import com.example.movieapp.model.request.LoginRequest;
import com.example.movieapp.model.request.RegisterRequest;
import com.example.movieapp.repository.UserRepository;
import com.example.movieapp.utils.StringUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession session;
    private final AuthenticationManager authenticationManager;


    public void login(LoginRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );

        try {
            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute(Constant.SESSION_NAME, authentication.getName());
        } catch (AuthenticationException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void register(RegisterRequest request) {
        // check email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email đã tồn tại");
        }

        // check password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu không khớp");
        }

        // create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setAvatar(StringUtils.generateLinkImage(request.getName()));

        userRepository.save(user);
        log.info("New user registered: {}", user);
    }
}
