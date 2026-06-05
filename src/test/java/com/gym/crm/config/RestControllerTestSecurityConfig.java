package com.gym.crm.config;

import com.gym.crm.security.JwtAccessDeniedHandler;
import com.gym.crm.security.JwtAuthenticationEntryPoint;
import com.gym.crm.security.JwtAuthenticationFilter;
import com.gym.crm.security.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;

import java.io.IOException;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Import({SecurityConfig.class, JwtAuthenticationEntryPoint.class, JwtAccessDeniedHandler.class})
public class RestControllerTestSecurityConfig {

    @Bean
    @Primary
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtAuthenticationEntryPoint entryPoint) {
        return new FakeJwtAuthenticationFilter(entryPoint);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> disableJwtAuthFilerRegistrationInServletContainer(JwtAuthenticationFilter jwtAuthenticationFilter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(jwtAuthenticationFilter);
        registration.setEnabled(false);

        return registration;
    }

    private static class FakeJwtAuthenticationFilter extends JwtAuthenticationFilter {

        public FakeJwtAuthenticationFilter(JwtAuthenticationEntryPoint entryPoint) {
            super(mock(AuthenticationManager.class), entryPoint);
        }

        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest request,
                                        @NonNull HttpServletResponse response,
                                        @NonNull FilterChain filterChain) throws ServletException, IOException {
            filterChain.doFilter(request, response);
        }
    }

}
