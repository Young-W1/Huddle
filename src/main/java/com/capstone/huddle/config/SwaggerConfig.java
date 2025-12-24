package com.capstone.huddle.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * OpenAPI/Swagger Configuration for comprehensive API documentation.
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:6061}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(serverList())
                .tags(tagList())
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createSecurityScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("Huddle API")
                .version("1.0.0")
                .description("""
                        ## Huddle - Knowledge Sharing & Content Discovery Platform
                        
                        Huddle is a comprehensive knowledge-sharing platform that allows users to:
                        - Create, read, update, and delete articles
                        - Comment on articles and engage in discussions
                        - Vote on content (upvote/downvote)
                        - Rate articles
                        - Follow other users
                        - Report inappropriate content
                        - Search across articles and users
                        
                        ### Authentication
                        Most endpoints require JWT authentication. To authenticate:
                        1. Call POST /huddle/login with username and password
                        2. Copy the JWT token from the response
                        3. Click 'Authorize' button above and enter: Bearer {your_token}
                        
                        ### Rate Limiting
                        - 60 requests per minute per IP
                        - 1000 requests per hour per IP
                        
                        ### Pagination
                        List endpoints support pagination with:
                        - `page`: Page number (0-based, default: 0)
                        - `size`: Items per page (default: 10, max: 100)
                        - `sort`: Sort field and direction (e.g., `createdAt,desc`)
                        """)
                .contact(new Contact()
                        .name("Huddle Support")
                        .email("support@huddle.com")
                        .url("https://huddle.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private List<Server> serverList() {
        return Arrays.asList(
                new Server()
                        .url("http://localhost:" + serverPort)
                        .description("Development Server"),
                new Server()
                        .url("https://huddle-api.onrender.com")
                        .description("Production Server")
        );
    }

    private List<Tag> tagList() {
        return Arrays.asList(
                new Tag().name("Authentication").description("User authentication endpoints"),
                new Tag().name("Users").description("User management and profile endpoints"),
                new Tag().name("Articles").description("Article CRUD operations"),
                new Tag().name("Comments").description("Comment management"),
                new Tag().name("Votes").description("Voting on comments"),
                new Tag().name("Ratings").description("Article ratings"),
                new Tag().name("Reports").description("Content reporting"),
                new Tag().name("Search").description("Search functionality"),
                new Tag().name("Analytics").description("Analytics and statistics"),
                new Tag().name("Notifications").description("User notifications")
        );
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("Enter your JWT token obtained from /huddle/login endpoint");
    }
}
