package com.capstone.huddle.articles.controller;

import com.capstone.huddle.articles.dto.ArticleFilterDto;
import com.capstone.huddle.articles.dto.request.ArticleDataDto;
import com.capstone.huddle.articles.dto.request.ArticleRequest;
import com.capstone.huddle.articles.dto.response.ArticleResponse;
import com.capstone.huddle.articles.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/huddle/articles")
@Tag(name = "Articles", description = "Article management APIs")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/create")
    @Operation(summary = "Create article", description = "Create a new article in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully creates article"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createArticle(@Valid @RequestBody ArticleRequest article,
                                           Authentication authentication) {
        try {
            String username = authentication.getName();
            ArticleDataDto createdArticle = articleService.createArticle(article, username);

            ArticleResponse<ArticleDataDto> response = ArticleResponse.<ArticleDataDto>builder()
                    .success(true)
                    .message("Article created successfully")
                    .data(createdArticle)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating article: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .success(false)
                    .message("Failed to create article: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/allArticles")
    @Operation(summary = "Get all articles", description = "Retrieve all articles from the database with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved articles"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid pagination parameters"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllArticles(Pageable pageable) {
        try {
            Page<ArticleDataDto> articles = articleService.getAllArticles(pageable);

            ArticleResponse<Page<ArticleDataDto>> response = ArticleResponse.<Page<ArticleDataDto>>builder()
                    .success(true)
                    .message("Articles retrieved successfully")
                    .data(articles)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving articles: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .success(false)
                    .message("Failed to retrieve articles: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("article/{id}")
    @Operation(summary = "Get article by ID", description = "Retrieve an article by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved article"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getArticleById(@PathVariable UUID id) {
        try {
            ArticleDataDto article = articleService.getArticleById(id);

            ArticleResponse<ArticleDataDto> response = ArticleResponse.<ArticleDataDto>builder()
                    .success(true)
                    .message("Article retrieved successfully")
                    .data(article)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving article by ID: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .success(false)
                    .message("Article not found: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(errorResponse);
        }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update article", description = "Update an existing article in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated article"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not the author"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateArticle(@PathVariable UUID id,
                                           @Valid @RequestBody ArticleRequest article,
                                           Authentication authentication) {
        try {
            String username = authentication.getName();
            ArticleDataDto updatedArticle = articleService.updateArticle(id, article, username);

            ArticleResponse<ArticleDataDto> response = ArticleResponse.<ArticleDataDto>builder()
                    .success(true)
                    .message("Article updated successfully")
                    .data(updatedArticle)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating article: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .success(false)
                    .message("Failed to update article: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete article", description = "Delete an article by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted article"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not the author"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteArticle(@PathVariable UUID id,
                                           Authentication authentication) {
        try {
            String username = authentication.getName();
            articleService.deleteArticle(id, username);

            ArticleResponse<Object> response = ArticleResponse.builder()
                    .success(true)
                    .message("Article deleted successfully")
                    .data(null)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting article: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .success(false)
                    .message("Failed to delete article: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter articles", description = "Search articles with various filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Articles retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> searchArticles(
            @ModelAttribute ArticleFilterDto filter,
            Pageable pageable) {
        try {
            Page<ArticleDataDto> results = articleService.searchArticles(filter, pageable);

            ArticleResponse<Page<ArticleDataDto>> response = ArticleResponse.<Page<ArticleDataDto>>builder()
                    .success(true)
                    .message("Search results retrieved successfully")
                    .data(results)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching articles: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .success(false)
                    .message("Failed to search articles: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/my-articles")
    @Operation(summary = "Get my articles", description = "Get all articles created by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved articles"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getMyArticles(Pageable pageable, Authentication authentication) {
        try {
            String username = authentication.getName();
            Page<ArticleDataDto> userArticles = articleService.getArticlesByAuthor(username, pageable);

            ArticleResponse<Page<ArticleDataDto>> response = ArticleResponse.<Page<ArticleDataDto>>builder()
                    .success(true)
                    .message("User articles retrieved successfully")
                    .data(userArticles)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving user articles: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .success(false)
                    .message("Failed to retrieve articles: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
