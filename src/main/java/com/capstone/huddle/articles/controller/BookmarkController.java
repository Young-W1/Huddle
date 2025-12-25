package com.capstone.huddle.articles.controller;

import com.capstone.huddle.articles.dto.request.ArticleDataDto;
import com.capstone.huddle.articles.service.BookmarkService;
import com.capstone.huddle.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/huddle/bookmarks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bookmarks", description = "Article bookmark management endpoints")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "Add bookmark", description = "Bookmark an article for later reading")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Article bookmarked successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Article already bookmarked"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Article not found")
    })
    @PostMapping("/{articleId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addBookmark(
            @PathVariable UUID articleId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            bookmarkService.addBookmark(username, articleId);

            return ResponseEntity.ok(ApiResponse.success("Article bookmarked successfully",
                    Map.of("articleId", articleId, "bookmarked", true)));
        } catch (RuntimeException e) {
            log.error("Error adding bookmark: ", e);
            // Allow "already bookmarked" message through as it's user-friendly
            if (e.getMessage() != null && e.getMessage().contains("already bookmarked")) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Article is already bookmarked"));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to bookmark article"));
        }
    }

    @Operation(summary = "Remove bookmark", description = "Remove a bookmark from an article")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bookmark removed successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Bookmark not found")
    })
    @DeleteMapping("/{articleId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> removeBookmark(
            @PathVariable UUID articleId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            bookmarkService.removeBookmark(username, articleId);

            return ResponseEntity.ok(ApiResponse.success("Bookmark removed successfully",
                    Map.of("articleId", articleId, "bookmarked", false)));
        } catch (Exception e) {
            log.error("Error removing bookmark: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to remove bookmark"));
        }
    }

    @Operation(summary = "Check bookmark status", description = "Check if an article is bookmarked by the current user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bookmark status retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{articleId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> isBookmarked(
            @PathVariable UUID articleId,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean isBookmarked = bookmarkService.isBookmarked(username, articleId);

            return ResponseEntity.ok(ApiResponse.success("Bookmark status retrieved",
                    Map.of("articleId", articleId, "bookmarked", isBookmarked)));
        } catch (Exception e) {
            log.error("Error checking bookmark status: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to check bookmark status"));
        }
    }

    @Operation(summary = "Get user bookmarks", description = "Get all bookmarked articles for the current user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bookmarks retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ArticleDataDto>>> getUserBookmarks(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        try {
            String username = authentication.getName();
            Page<ArticleDataDto> bookmarks = bookmarkService.getUserBookmarks(username, pageable);

            return ResponseEntity.ok(ApiResponse.success("Bookmarks retrieved successfully", bookmarks));
        } catch (Exception e) {
            log.error("Error getting bookmarks: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to retrieve bookmarks. Please try again."));
        }
    }

    @Operation(summary = "Get bookmark count", description = "Get the total number of bookmarks for the current user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Bookmark count retrieved"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getBookmarkCount(Authentication authentication) {
        try {
            String username = authentication.getName();
            long count = bookmarkService.getBookmarkCount(username);

            return ResponseEntity.ok(ApiResponse.success("Bookmark count retrieved",
                    Map.of("count", count)));
        } catch (Exception e) {
            log.error("Error getting bookmark count: ", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get bookmark count"));
        }
    }
}

