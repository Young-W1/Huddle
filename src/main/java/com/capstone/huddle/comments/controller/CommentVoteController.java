package com.capstone.huddle.comments.controller;

import com.capstone.huddle.comments.dto.response.CommentsResponse;
import com.capstone.huddle.comments.enums.VoteType;
import com.capstone.huddle.comments.model.CommentVoteEntity;
import com.capstone.huddle.comments.service.CommentVoteService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comment Voting", description = "Comment Voting API")
public class CommentVoteController {

    private final CommentVoteService commentVoteService;

    @PostMapping("/huddle/comments/{commentId}/vote")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully voted on comment or vote removed"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - authentication required"),
            @ApiResponse(responseCode = "404", description = "Comment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> voteComment(@PathVariable UUID commentId, Principal principal, @RequestParam VoteType voteType) {
        CommentVoteEntity commentVote = commentVoteService.voteComment(commentId, principal.getName(), voteType);
        if (commentVote == null) {
            return ResponseEntity.ok(new CommentsResponse<>(true, "Vote removed successfully", null));
        }
        return ResponseEntity.ok(new CommentsResponse<>(true, "Vote recorded successfully", commentVote));
    }
}
