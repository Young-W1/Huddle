package com.capstone.huddle.users.security;

import com.capstone.huddle.utils.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public authentication endpoints
                        .requestMatchers("/", "/huddle/signup", "/huddle/login", "/huddle/logout")
                        .permitAll()

                        // Public access to uploaded files (profile pictures, etc.)
                        .requestMatchers("/huddle/uploads/**", "/huddle/uploads/profiles/**")
                        .permitAll()

                        // Swagger documentation endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                        .permitAll()

                        // Public article viewing endpoints
                        .requestMatchers(HttpMethod.GET, "/huddle/articles/allArticles",
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
                        .requestMatchers(HttpMethod.POST, "/huddle/articles/create")
                        .authenticated()
                        .requestMatchers(HttpMethod.PUT, "/huddle/articles/update/{id}")
                        .authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/huddle/articles/delete/{id}")
                        .authenticated()

                        // Comment management endpoints - require authentication
                        .requestMatchers(HttpMethod.POST, "/huddle/articles/{articleId}/comments")
                        .authenticated()
                        .requestMatchers(HttpMethod.PUT, "/huddle/articles/{articleId}/comments/{commentId}")
                        .authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/huddle/articles/{articleId}/comments/{commentId}")
                        .authenticated()

                        // Voting and rating endpoints - require authentication
                        .requestMatchers("/huddle/articles/{articleId}/comments/{commentId}/vote",
                                "/huddle/articles/{articleId}/rate")
                        .authenticated()

                        // Profile management endpoints - require authentication
                        .requestMatchers(HttpMethod.PUT, "/huddle/users/profile")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST,
                                "/huddle/users/profile/picture",  // Profile picture upload endpoint
                                "/huddle/users/{userId}/follow")
                        .authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/huddle/users/{userId}/unfollow")
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

                        // Report management endpoints
                        .requestMatchers(HttpMethod.POST, "/huddle/reports")
                        .authenticated()  // Any authenticated user can create a report

                        .requestMatchers(HttpMethod.GET, "/huddle/reports", "/huddle/reports/**")
                        .hasAuthority("ADMIN")  // Only admins can view reports

                        .requestMatchers(HttpMethod.PUT, "/huddle/reports/{reportId}")
                        .hasAuthority("ADMIN")  // Only admins can update report status

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