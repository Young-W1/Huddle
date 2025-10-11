package com.capstone.huddle.comments.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentsRequest {

    @NotBlank(message = "Comment body cannot be empty")
    private String body;

    private UUID parentCommentId;
}
