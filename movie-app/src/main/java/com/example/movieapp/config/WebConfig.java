package com.example.movieapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;
    private final RoleBasedAuthInterceptor roleBasedAuthInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image_uploads/**").addResourceLocations("file:image_uploads/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/reviews/**") // Áp dụng cho tất cả các URL
                .excludePathPatterns("/dang-nhap", "/dang-ky", "/css/**", "/js/**", "/admin-assets/**", "/api/videos/**", "/image_uploads/**"); // Trừ các URL không cần kiểm tra
        registry.addInterceptor(roleBasedAuthInterceptor)
                .addPathPatterns("/admin", "/admin/**", "/api/admin/**") // Áp dụng cho tất cả các URL
                .excludePathPatterns("/login", "/register", "/css/**", "/js/**", "/admin-assets/**", "/api/videos/**", "/image_uploads/**"); // Trừ các URL không cần kiểm tra;
    }
}