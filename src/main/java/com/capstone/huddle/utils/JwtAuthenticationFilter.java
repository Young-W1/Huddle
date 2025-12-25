package com.capstone.huddle.utils;

import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    String username = jwtUtil.extractUsername(token);

                    if (logger.isDebugEnabled()) {
                        logger.debug("Extracted username: " + username);
                    }

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        if (jwtUtil.validateToken(token, username)) {
                            // Fetch user from database to get roles
                            UserEntity user = userRepository.findByUsername(username).orElse(null);

                            if (user != null) {
                                // Convert roles to authorities
                                var authorities = user.getRoles().stream()
                                        .map(role -> new SimpleGrantedAuthority(role.name()))
                                        .collect(Collectors.toList());

                                if (logger.isDebugEnabled()) {
                                    logger.debug("User " + username + " has authorities: " + authorities);
                                }

                                UsernamePasswordAuthenticationToken authToken =
                                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authToken);

                                if (logger.isDebugEnabled()) {
                                    logger.debug("Authentication successful for user: " + username + " with authorities: " + authorities);
                                }
                            } else {
                                logger.error("User not found in database: " + username);
                            }
                        }
                    }
                } catch (ClassCastException e) {
                    logger.error("ClassCastException in JWT processing: " + e.getMessage(), e);
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication failed due to type mismatch");
                    return;
                } catch (Exception e) {
                    logger.error("Error processing JWT token", e);
                    // Continue without authentication - let Spring Security handle it
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("Unexpected error in authentication filter", e);
            throw e;
        }
    }
}
