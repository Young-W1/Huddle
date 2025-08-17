package com.capstone.huddle.comments.controller;

import com.capstone.huddle.comments.dto.request.CommentsRequest;
import com.capstone.huddle.comments.dto.response.CommentsResponse;
import com.capstone.huddle.comments.model.CommentsEntity;
import com.capstone.huddle.comments.service.CommentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@Slf4j
@RequestMapping()
@Tag(name = "Comments", description = "Comment management APIs")

public class CommentsController {

    @Autowired
    private CommentsService commentsService;

    //create comment
    @PostMapping("/huddle/articles/{articleId}/comments")
    @Operation(summary = "Create comment", description = "Create a new comment for an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully creates comment"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createComment(
            @PathVariable Long articleId,
            @RequestBody @Valid CommentsRequest commentsRequest,
            Principal principal) {

        log.info("Creating comment for article {} by user {}", articleId, principal.getName());

        try {
            CommentsEntity comment = commentsService.addComment(
                    articleId,
                    principal.getName(),
                    commentsRequest.getBody()
            );

            CommentsResponse<CommentsEntity> response = CommentsResponse.<CommentsEntity>builder()
                    .status(true)
                    .message("Comment created successfully")
                    .data(comment)
                    .build();

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("Entity not found: ", e);
            CommentsResponse<Object> errorResponse = CommentsResponse.builder()
                    .status(false)
                    .message("Failed to create comment: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            log.error("Error creating comment: ", e);
            CommentsResponse<Object> errorResponse = CommentsResponse.builder()
                    .status(false)
                    .message("Failed to create comment: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // get all comments for an article
    @GetMapping("/huddle/articles/{articleId}/comments")
    @Operation(summary = "Get comments", description = "Get all comments for an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved comments"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getCommentsByArticle(@PathVariable Long articleId) {
        log.info("Retrieving comments for article ID: {}", articleId);

        try {
            var response = commentsService.getCommentsByArticleId(articleId);

            CommentsResponse<List<CommentsEntity>> commentsResponse = CommentsResponse.<List<CommentsEntity>>builder()
                    .status(true)
                    .message(response.getMessage())
                    .data(response.getData())
                    .build();

            return ResponseEntity.ok(commentsResponse);
        } catch (EntityNotFoundException e) {
            log.error("Article not found with ID: {}", articleId);
            CommentsResponse<Object> errorResponse = CommentsResponse.builder()
                    .status(false)
                    .message("Article not found: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            log.error("Error retrieving comments for article {}: {}", articleId, e.getMessage());
            CommentsResponse<Object> errorResponse = CommentsResponse.builder()
                    .status(false)
                    .message("Failed to retrieve comments: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PutMapping("/huddle/articles/{articleId}/comments/{commentId}")
    @Operation(summary = "Update comment", description = "Update an existing comment. Only the owner can update.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated comment"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not the comment owner"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateComment(
            @PathVariable Long articleId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentsRequest commentsRequest,
            Principal principal) {

        log.info("User {} wants to update comment {} on article {}", principal.getName(), commentId, articleId);

        try {
            CommentsResponse<CommentsEntity> response = commentsService.updateComment(
                    commentId,
                    principal.getName(),
                    commentsRequest.getBody()
            );
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("Comment not found: {}", commentId);
            CommentsResponse<Object> errorResponse = CommentsResponse.builder()
                    .status(false)
                    .message("Comment not found: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(errorResponse);
        } catch (SecurityException e) {
            log.error("Unauthorized update attempt by user {}: {}", principal.getName(), e.getMessage());
            CommentsResponse<Object> errorResponse = CommentsResponse.builder()
                    .status(false)
                    .message("Forbidden: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(403).body(errorResponse);
        } catch (Exception e) {
            log.error("Error updating comment {}: {}", commentId, e.getMessage());
            CommentsResponse<Object> errorResponse = CommentsResponse.builder()
                    .status(false)
                    .message("Failed to update comment: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

//delete comment
    @DeleteMapping("/huddle/articles/{articleId}/comments/{commentId}")
    @Operation(summary = "Delete comment", description = "Delete a comment. Only the owner can delete.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted comment"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not the comment owner"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CommentsResponse<Void>> deleteComment(
            @PathVariable Long articleId,
            @PathVariable Long commentId,
            Principal principal) {

        log.info("User {} wants to delete comment {} on article {}", principal.getName(), commentId, articleId);

        try {
            commentsService.deleteComment(commentId, principal.getName());

            CommentsResponse<Void> response = CommentsResponse.<Void>builder()
                    .status(true)
                    .message("Comment deleted successfully")
                    .data(null)
                    .build();

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("Comment not found: {}", commentId);
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            log.error("Unauthorized delete attempt by user {}: {}", principal.getName(), e.getMessage());
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            log.error("Error deleting comment {}: {}", commentId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }




}
