package com.capstone.huddle.users.security;

import com.capstone.huddle.utils.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public authentication endpoints
                        .requestMatchers("/", "/huddle/signup", "/huddle/login", "/huddle/logout")
                        .permitAll()

                        // Swagger documentation endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                        .permitAll()

                        // Public article viewing endpoints
                        .requestMatchers("GET", "/huddle/articles/allArticles",
                                "/huddle/articles/article/{id}",
                                "/huddle/articles/{articleId}/comments")
                        .permitAll()

                        // Public profile viewing endpoints
                        .requestMatchers(HttpMethod.GET,
                                "/huddle/users/{userId}/profile",
                                "/huddle/users/{userId}/followers",
                                "/huddle/users/{userId}/following")
                        .permitAll()

                        // Article management endpoints - require authentication
                        .requestMatchers("POST", "/huddle/articles/create")
                        .authenticated()
                        .requestMatchers("PUT", "/huddle/articles/update/{id}")
                        .authenticated()
                        .requestMatchers("DELETE", "/huddle/articles/delete/{id}")
                        .authenticated()

                        // Comment management endpoints - require authentication
                        .requestMatchers("POST", "/huddle/articles/{articleId}/comments")
                        .authenticated()
                        .requestMatchers("PUT", "/huddle/articles/{articleId}/comments/{commentId}")
                        .authenticated()
                        .requestMatchers("DELETE", "/huddle/articles/{articleId}/comments/{commentId}")
                        .authenticated()

                        // Voting and rating endpoints - require authentication
                        .requestMatchers("/huddle/articles/{articleId}/comments/{commentId}/vote",
                                "/huddle/articles/{articleId}/rate")
                        .authenticated()

                        // Profile management endpoints - require authentication
                        .requestMatchers("PUT", "/huddle/users/profile")
                        .authenticated()
                        .requestMatchers("POST", "/huddle/users/{userId}/follow",
                                "/huddle/users/{userId}/unfollow")
                        .authenticated()


                        // Notification endpoints - require authentication
                        .requestMatchers("/huddle/notifications/**")
                        .authenticated()

                        // Search endpoints
                        .requestMatchers(HttpMethod.GET, "/huddle/articles/search")
                        .permitAll()  // Allow public article search

                        .requestMatchers(HttpMethod.GET,
                                "/huddle/search/global",
                                "/huddle/search-users",
                                "/huddle/notifications/search")
                        .authenticated()  // Require authentication for user/notification search and global search

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}