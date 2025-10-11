package com.capstone.huddle.comments.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommentResponseDto {
    private UUID id;
    private String body;
    private String authorUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID articleId;
    private String articleTitle;
    private UUID parentCommentId;
    private Integer upvotes;
    private Integer downvotes;
}
