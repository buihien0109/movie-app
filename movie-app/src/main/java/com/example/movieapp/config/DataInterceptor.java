package com.example.movieapp.config;

import com.example.movieapp.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class DataInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            log.info("DataInterceptor:postHandle");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
                log.info("DataInterceptor:postHandle:isLogined");
                CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
                modelAndView.addObject("userLogined_name", principal.getUser().getName());
                modelAndView.addObject("userLogined_avatar", principal.getUser().getAvatar());
                modelAndView.addObject("isLogined", true);
            } else {
                log.info("DataInterceptor:postHandle:isNotLogined");
                modelAndView.addObject("isLogined", false);
            }
        }
    }
}
