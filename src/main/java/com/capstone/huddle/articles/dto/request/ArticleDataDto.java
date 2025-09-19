package com.capstone.huddle.articles.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDataDto {

    private UUID id;
    private String title;
    private String content;
    private String category;
    private Integer views;

    @JsonProperty("authorId")
    private UUID authorId;

    @JsonProperty("authorUsername")
    private String authorUsername;

    @JsonProperty("authorName")
    private String authorName;

    @JsonProperty("authorProfilePicture")
    private String authorProfilePicture;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double averageRating;
    private Integer totalRatings;
    private Integer commentCount;
}
