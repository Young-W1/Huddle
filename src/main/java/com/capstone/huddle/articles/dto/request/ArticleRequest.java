package com.capstone.huddle.articles.dto.request;

import com.capstone.huddle.articles.model.ArticleStatus;
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
    private ArticleStatus status;

}
