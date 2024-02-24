package com.example.movieapp.security;

import com.example.movieapp.constant.Constant;
import com.example.movieapp.security.error.CustomAccessDeniedHandler;
import com.example.movieapp.security.error.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final AuthenticationProvider authenticationProvider;
    private final CustomFilter customFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/css/**", "/js/**", "/image/**", "/admin-assets/**", "/image_uploads/**").permitAll();
            auth.requestMatchers("/", "/phim-bo", "/phim-chieu-rap", "/phim-le", "/tin-tuc", "/tin-tuc/**", "/phim/**", "/xem-phim/**", "/cua-hang", "/cua-hang/**", "/dang-nhap", "/dang-ky", "/quen-mat-khau", "/xac-thuc-tai-khoan", "/dat-lai-mat-khau", "/quoc-gia/**", "/the-loai/**").permitAll();
            auth.requestMatchers("/api/auth/**", "/api/view-film-logs").permitAll();
            auth.requestMatchers(HttpMethod.GET, "/api/videos/**").permitAll();
            auth.requestMatchers(HttpMethod.POST, "/api/videos").hasRole("ADMIN");
            auth.requestMatchers(HttpMethod.DELETE, "/api/videos/**").hasRole("ADMIN");
            auth.requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN");
            auth.requestMatchers("/api/reviews", "/api/reviews/**").hasAnyRole("ADMIN", "USER");
            auth.anyRequest().authenticated();
        });
        http.logout(logout -> {
            logout.deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
                    .permitAll()
                    .clearAuthentication(true)
                    .logoutSuccessHandler((request, response, authentication) -> {
                        request.getSession().setAttribute(Constant.SESSION_NAME, null);
                        request.getSession().invalidate();
                        response.setStatus(HttpStatus.OK.value());
                    });
        });
        http.exceptionHandling(exceptionHandling -> {
            exceptionHandling.accessDeniedHandler(customAccessDeniedHandler);
            exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint);
        });
        http.authenticationProvider(authenticationProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers -> {
            headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
        });
        return http.build();
    }
}
