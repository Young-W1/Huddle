package com.capstone.huddle.comments.controller;

import com.capstone.huddle.articles.dto.response.ArticleResponse;
import com.capstone.huddle.comments.dto.request.CommentsRequest;
import com.capstone.huddle.comments.dto.response.CommentResponseDto;
import com.capstone.huddle.comments.dto.response.CommentsResponse;
import com.capstone.huddle.comments.service.CommentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/huddle/articles/{articleId}/comments")
@Tag(name = "Comments", description = "Comment management APIs")
@RequiredArgsConstructor
public class CommentsController {

    private final CommentsService commentsService;

    @PostMapping
    @Operation(summary = "Create comment", description = "Create a new comment for an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully creates comment"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Article or user not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CommentsResponse<CommentResponseDto>> createComment(
            @PathVariable UUID articleId,
            @RequestBody @Valid CommentsRequest commentsRequest,
            Principal principal) {

        log.info("Creating comment for article {} by user {}", articleId, principal.getName());

        try {
            CommentResponseDto comment = commentsService.addComment(
                    articleId,
                    principal.getName(),
                    commentsRequest.getBody()
            );

            CommentsResponse<CommentResponseDto> response = CommentsResponse.<CommentResponseDto>builder()
                    .success(true)
                    .message("Comment created successfully")
                    .data(comment)
                    .build();

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("Entity not found: ", e);
            CommentsResponse<CommentResponseDto> errorResponse = CommentsResponse.<CommentResponseDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            log.error("Error creating comment: ", e);
            CommentsResponse<CommentResponseDto> errorResponse = CommentsResponse.<CommentResponseDto>builder()
                    .success(false)
                    .message("Failed to create comment: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping
    @Operation(summary = "Get comments", description = "Get all comments for an article with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comments"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ArticleResponse<Page<CommentResponseDto>>> getCommentsByArticle(
            @PathVariable UUID articleId,
            Pageable pageable) {

        log.info("Retrieving comments for article ID: {} with page: {}, size: {}",
                articleId, pageable.getPageNumber(), pageable.getPageSize());

        try {
            ArticleResponse<Page<CommentResponseDto>> response = commentsService.getCommentsByArticleId(articleId, pageable);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("Article not found with ID: {}", articleId);
            ArticleResponse<Page<CommentResponseDto>> errorResponse = ArticleResponse.<Page<CommentResponseDto>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            log.error("Error retrieving comments for article {}: {}", articleId, e.getMessage());
            ArticleResponse<Page<CommentResponseDto>> errorResponse = ArticleResponse.<Page<CommentResponseDto>>builder()
                    .success(false)
                    .message("Failed to retrieve comments: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Update comment", description = "Update an existing comment. Only the owner can update.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated comment"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not the comment owner"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CommentsResponse<CommentResponseDto>> updateComment(
            @PathVariable UUID articleId,
            @PathVariable UUID commentId,
            @RequestBody @Valid CommentsRequest commentsRequest,
            Principal principal) {

        log.info("User {} wants to update comment {} on article {}", principal.getName(), commentId, articleId);

        try {
            CommentsResponse<CommentResponseDto> response = commentsService.updateComment(
                    commentId,
                    principal.getName(),
                    commentsRequest.getBody()
            );
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("Comment not found: {}", commentId);
            CommentsResponse<CommentResponseDto> errorResponse = CommentsResponse.<CommentResponseDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(errorResponse);
        } catch (SecurityException e) {
            log.error("Unauthorized update attempt by user {}: {}", principal.getName(), e.getMessage());
            CommentsResponse<CommentResponseDto> errorResponse = CommentsResponse.<CommentResponseDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(403).body(errorResponse);
        } catch (Exception e) {
            log.error("Error updating comment {}: {}", commentId, e.getMessage());
            CommentsResponse<CommentResponseDto> errorResponse = CommentsResponse.<CommentResponseDto>builder()
                    .success(false)
                    .message("Failed to update comment: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment", description = "Delete a comment. Only the owner can delete.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted comment"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not the comment owner"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CommentsResponse<Void>> deleteComment(
            @PathVariable UUID articleId,
            @PathVariable UUID commentId,
            Principal principal) {

        log.info("User {} wants to delete comment {} on article {}", principal.getName(), commentId, articleId);

        try {
            commentsService.deleteComment(commentId, principal.getName());

            CommentsResponse<Void> response = CommentsResponse.<Void>builder()
                    .success(true)
                    .message("Comment deleted successfully")
                    .data(null)
                    .build();

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("Comment not found: {}", commentId);
            CommentsResponse<Void> errorResponse = CommentsResponse.<Void>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(errorResponse);
        } catch (SecurityException e) {
            log.error("Unauthorized delete attempt by user {}: {}", principal.getName(), e.getMessage());
            CommentsResponse<Void> errorResponse = CommentsResponse.<Void>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(403).body(errorResponse);
        } catch (Exception e) {
            log.error("Error deleting comment {}: {}", commentId, e.getMessage());
            CommentsResponse<Void> errorResponse = CommentsResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete comment: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}