package com.example.movieapp.config;

import com.example.movieapp.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RoleBasedAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("RoleBasedAuthInterceptor.preHandle");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return false;
        }

        String role = user.getRole().getValue();
        log.info("role: {}", role);

        String requestURI = request.getRequestURI();
        log.info("requestURI: {}", requestURI);

        if (requestURI.startsWith("/admin") && !role.equals("ROLE_ADMIN")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
            return false;
        }

        return true;
    }
}
