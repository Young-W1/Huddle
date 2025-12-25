package com.capstone.huddle.comments.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentsResponse<T> {

    private boolean success;
    private String message;
    private T data;
}
