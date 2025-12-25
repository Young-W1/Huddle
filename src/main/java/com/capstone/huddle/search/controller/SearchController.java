package com.capstone.huddle.search.controller;

import com.capstone.huddle.articles.dto.ArticleFilterDto;
import com.capstone.huddle.articles.service.ArticleService;
import com.capstone.huddle.notifications.dto.NotificationFilterDto;
import com.capstone.huddle.notifications.service.NotificationService;
import com.capstone.huddle.users.dto.UserFilterDto;
import com.capstone.huddle.users.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Search", description = "Global search endpoints for users, articles, and notifications")
@RequestMapping("/huddle/search")
@RequiredArgsConstructor

public class SearchController {

    private final UserService userService;
    private final ArticleService articleService;
    private final NotificationService notificationService;

    @GetMapping("/global")
    @Operation(
            summary = "Global search",
            description = "Searches users, articles, and notifications by query string."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful search",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    type = "object",
                                    example = "{ \"users\": [...], \"articles\": [...], \"notifications\": [...] }"
                            )
                    )
            )
    })
    public ResponseEntity<Map<String, Object>> globalSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Map<String, Object> results = new HashMap<>();
        Pageable pageable = PageRequest.of(page, size);

        // Search users
        UserFilterDto userFilter = UserFilterDto.builder()
                .searchTerm(query)
                .build();
        results.put("users", userService.searchUsers(userFilter, pageable));

        // Search articles
        ArticleFilterDto articleFilter = ArticleFilterDto.builder()
                .searchTerm(query)
                .build();
        results.put("articles", articleService.searchArticles(articleFilter, pageable));

        // Search notifications
        NotificationFilterDto notificationFilter = NotificationFilterDto.builder()
                .searchTerm(query)
                .build();
        results.put("notifications", notificationService.searchNotifications(
                getCurrentUsername(), notificationFilter, pageable));

        return ResponseEntity.ok(results);
    }

    private String getCurrentUsername() {
        // Get from SecurityContext
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}

