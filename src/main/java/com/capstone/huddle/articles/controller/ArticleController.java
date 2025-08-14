package com.capstone.huddle.articles.controller;

import com.capstone.huddle.articles.dto.request.ArticleRequest;
import com.capstone.huddle.articles.dto.response.ArticleResponse;
import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createArticle(@Valid @RequestBody ArticleRequest article) {
        try {
            ArticleResponse<ArticleEntity> response = articleService.createArticle(article);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating article: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .status("400")
                    .message("Failed to create article: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/allArticles")
    @Operation(summary = "Get all articles", description = "Retrieve all articles from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved articles"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllArticles() {
        try {
            ArticleResponse<List<ArticleEntity>> response = articleService.getAllArticles();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving articles: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .status("500")
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
    public ResponseEntity<?> getArticleById(@PathVariable Long id) {
        try {
            ArticleResponse<ArticleEntity> response = articleService.getArticleById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving article by ID: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .status("404")
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
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleRequest article) {
        try {
            ArticleResponse<ArticleEntity> response = articleService.updateArticle(id, article);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating article: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .status("400")
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
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> deleteArticle(@PathVariable Long id) {
        try {
            ArticleResponse<Void> response = articleService.deleteArticle(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting article: ", e);
            ArticleResponse<Object> errorResponse = ArticleResponse.builder()
                    .status("400")
                    .message("Failed to delete article: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }





}
