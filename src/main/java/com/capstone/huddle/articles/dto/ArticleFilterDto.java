package com.capstone.huddle.articles.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ArticleFilterDto {
    private String searchTerm;
    private String authorUsername;

}
