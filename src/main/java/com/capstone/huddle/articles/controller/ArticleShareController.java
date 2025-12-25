package com.capstone.huddle.articles.controller;

import com.capstone.huddle.articles.model.ArticleShareEntity;
import com.capstone.huddle.articles.service.ArticleShareService;
import com.capstone.huddle.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/huddle/articles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Article Sharing", description = "Article sharing functionality endpoints")
public class ArticleShareController {

    private final ArticleShareService shareService;

    @Operation(summary = "Get share links", description = "Generate share links for various social media platforms")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Share links generated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Article not found")
    })
    @GetMapping("/{articleId}/share-links")
    public ResponseEntity<ApiResponse<Map<String, String>>> getShareLinks(
            @PathVariable UUID articleId,
            Authentication authentication) {
        try {
            String username = authentication != null ? authentication.getName() : "anonymous";
            Map<String, String> shareLinks = shareService.generateShareLinks(articleId, username);
            return ResponseEntity.ok(ApiResponse.success("Share links generated successfully", shareLinks));
        } catch (Exception e) {
            log.error("Error generating share links: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Record share", description = "Record when a user shares an article")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Share recorded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Article not found")
    })
    @PostMapping("/{articleId}/share")
    public ResponseEntity<ApiResponse<Map<String, Object>>> recordShare(
            @PathVariable UUID articleId,
            @RequestParam ArticleShareEntity.SharePlatform platform,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            shareService.recordShare(articleId, username, platform);

            return ResponseEntity.ok(ApiResponse.success("Share recorded successfully",
                    Map.of("articleId", articleId, "platform", platform.name())));
        } catch (Exception e) {
            log.error("Error recording share: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Get share count", description = "Get total share count for an article")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Share count retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Article not found")
    })
    @GetMapping("/{articleId}/share-count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getShareCount(@PathVariable UUID articleId) {
        try {
            long count = shareService.getShareCount(articleId);
            Map<String, Long> platformCounts = shareService.getShareCountByPlatform(articleId);

            return ResponseEntity.ok(ApiResponse.success("Share count retrieved successfully",
                    Map.of("totalShares", count, "byPlatform", platformCounts)));
        } catch (Exception e) {
            log.error("Error getting share count: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}

