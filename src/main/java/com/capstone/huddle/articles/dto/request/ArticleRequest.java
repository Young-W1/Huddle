package com.capstone.huddle.articles.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRequest {

    private String title;
    private String content;
    private String author;
    private String createdAt;
    private String updatedAt;


}
