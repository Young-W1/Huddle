package com.capstone.huddle.users.security;

import com.capstone.huddle.utils.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Simplified CSRF disable
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .authorizeHttpRequests(auth -> auth
                        // IMPORTANT: Order matters - most specific first

                        // Protected article operations - MUST come before general /articles/**
                        .requestMatchers("/articles/create").authenticated()
                        .requestMatchers("/articles/edit/**").authenticated()
                        .requestMatchers("/my-articles").authenticated()

                        // Protected user pages
                        .requestMatchers("/profile", "/notifications").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Protected API endpoints
                        .requestMatchers(HttpMethod.POST, "/huddle/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/huddle/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/huddle/**").authenticated()
                        .requestMatchers("/huddle/articles/my-articles").authenticated()
                        .requestMatchers("/huddle/articles/my-articles-session").authenticated()

                        // Public pages - AFTER protected patterns
                        .requestMatchers("/", "/index", "/home").permitAll()
                        .requestMatchers("/login", "/signup", "/logout").permitAll()
                        .requestMatchers("/articles", "/articles/{id:[a-f0-9\\-]+}").permitAll()  // UUID pattern

                        // Public API endpoints
                        .requestMatchers(HttpMethod.GET, "/huddle/articles/allArticles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/huddle/articles/article/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/huddle/articles/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/huddle/users/**").permitAll()

                        // Static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/webjars/**").permitAll()
                        .requestMatchers("/error", "/error/**", "/favicon.ico").permitAll()

                        // Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()

                        // Everything else is public
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            String requestUri = request.getRequestURI();
                            // Only redirect to login for protected pages
                            if (requestUri.startsWith("/my-articles") ||
                                    requestUri.startsWith("/profile") ||
                                    requestUri.startsWith("/notifications") ||
                                    requestUri.startsWith("/articles/create") ||
                                    requestUri.startsWith("/articles/edit")) {
                                response.sendRedirect("/login");
                            } else {
                                // For public pages, don't redirect
                                response.setStatus(200);
                            }
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
