package com.capstone.huddle.articles.controller;

import com.capstone.huddle.articles.dto.response.ArticleResponse;
import com.capstone.huddle.articles.model.ArticleRatingEntity;
import com.capstone.huddle.articles.service.ArticleRatingService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

// ArticleRatingController.java
@RestController
@RequiredArgsConstructor
@Tag(name = "Article Rating", description = "Article Rating APIs")
@Slf4j
public class ArticleRatingController {
    private final ArticleRatingService ratingService;

    @PostMapping("/huddle/articles/{articleId}/rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully rated article or reviewed article"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> rateArticle(
            @PathVariable UUID articleId,
            @RequestParam @Min(1) @Max(5) Integer rating,
            Principal principal) {
        ArticleRatingEntity articleRating = ratingService.rateArticle(articleId, principal.getName(), rating);
        return ResponseEntity.ok(new ArticleResponse<>(true, "Article rated successfully", articleRating));
    }
}
