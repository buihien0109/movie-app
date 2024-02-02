package com.example.movieapp.config;

import com.example.movieapp.constant.Constant;
import com.example.movieapp.repository.CountryRepository;
import com.example.movieapp.repository.GenreRepository;
import com.example.movieapp.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInterceptor implements HandlerInterceptor {
    private final CountryRepository countryRepository;
    private final GenreRepository genreRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
                modelAndView.addObject("userLogined_name", principal.getUser().getName());
                modelAndView.addObject("userLogined_avatar", principal.getUser().getAvatar());
                modelAndView.addObject("userLogined_role", principal.getUser().getRole().getValue());
                modelAndView.addObject("isLogined", true);
            } else {
                modelAndView.addObject("isLogined", false);
            }

            if (!Objects.requireNonNull(modelAndView.getViewName()).startsWith("admin")) {
                modelAndView.addObject("genres", genreRepository.findAll());
                modelAndView.addObject("countries", countryRepository.findAll());
            }
        }
    }
}
