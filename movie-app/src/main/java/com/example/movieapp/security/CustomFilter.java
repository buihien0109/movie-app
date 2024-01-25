package com.example.movieapp.security;

import com.example.movieapp.constant.Constant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // Danh sách các đường dẫn permitAll
        List<String> permitAllPaths = Arrays.asList("/css/", "/js/", "/image/", "/admin-assets/", "/image_upload");

        // Kiểm tra xem requestURI có nằm trong danh sách permitAll không
        boolean isPermitAllPath = permitAllPaths.stream().anyMatch(requestURI::startsWith);

        if (isPermitAllPath) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy ra email trong session
        String userEmail = (String) request.getSession().getAttribute(Constant.SESSION_NAME);
        if (userEmail == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy ra thông tin của user
        log.info("RequestURI: {}", requestURI);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        // Tạo đối tượng phân quyền
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
