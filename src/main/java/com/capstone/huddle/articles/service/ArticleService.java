package com.capstone.huddle.articles.service;

import com.capstone.huddle.articles.dto.request.ArticleRequest;
import com.capstone.huddle.articles.dto.response.ArticleResponse;
import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ArticleResponse<ArticleEntity> createArticle(ArticleRequest articleRequest) {
        log.info("Creating new article {}", articleRequest.getTitle());

        // Get the username from security context
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find the user entity
        UserEntity author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Create the article entity
        ArticleEntity article = ArticleEntity.builder()
                .title(articleRequest.getTitle())
                .content(articleRequest.getContent())
                .author(author)
                .build();

        // Save the article
        ArticleEntity savedArticle = articleRepository.save(article);

        log.info("Article created successfully with id: {}", savedArticle.getId());

        return ArticleResponse.<ArticleEntity>builder()
                .status("success")
                .message("Article created successfully")
                .data(savedArticle)
                .build();
    }

    @Transactional(readOnly = true)
    public ArticleResponse<List<ArticleEntity>> getAllArticles() {
        log.info("Retrieving all articles");

        try {
            List<ArticleEntity> articles = articleRepository.findAll();

            log.info("Retrieved {} articles", articles.size());

            return ArticleResponse.<List<ArticleEntity>>builder()
                    .status("success")
                    .message("Articles retrieved successfully")
                    .data(articles)
                    .build();
        } catch (Exception e) {
            log.error("Error retrieving articles: ", e);
            throw new RuntimeException("Failed to retrieve articles: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ArticleResponse<ArticleEntity> getArticleById(Long id) {
        log.info("Retrieving article by ID: {}", id);
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + id));
        log.info("Article retrieved successfully with ID: {}", article.getId());
        return ArticleResponse.<ArticleEntity>builder()
                .status("success")
                .message("Article retrieved successfully")
                .data(article)
                .build();
    }

    @Transactional
    public ArticleResponse<ArticleEntity> updateArticle(Long id, ArticleRequest articleRequest) {
        log.info("Updating article with ID: {}", id);
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + id));
        // Update article fields
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        // Save updated article
        ArticleEntity updatedArticle = articleRepository.save(article);
        log.info("Article updated successfully with ID: {}", updatedArticle.getId());
        return ArticleResponse.<ArticleEntity>builder()
                .status("success")
                .message("Article updated successfully")
                .data(updatedArticle)
                .build();
    }

    @Transactional
    public ArticleResponse<Void> deleteArticle(Long id) {
        log.info("Deleting article with ID: {}", id);
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + id));
        // Delete the article
        articleRepository.delete(article);
        log.info("Article deleted successfully with ID: {}", id);
        return ArticleResponse.<Void>builder()
                .status("success")
                .message("Article deleted successfully")
                .data(null)
                .build();
    }
}
