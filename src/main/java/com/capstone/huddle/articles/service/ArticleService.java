package com.capstone.huddle.articles.service;

import com.capstone.huddle.articles.dto.ArticleFilterDto;
import com.capstone.huddle.articles.dto.request.ArticleDataDto;
import com.capstone.huddle.articles.dto.request.ArticleRequest;
import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.common.specification.GenericSpecificationBuilder;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ArticleDataDto createArticle(ArticleRequest articleRequest, String username) {
        log.info("Creating new article '{}' for user: {}", articleRequest.getTitle(), username);

        UserEntity author = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        ArticleEntity article = ArticleEntity.builder()
                .title(articleRequest.getTitle())
                .content(articleRequest.getContent())
                .author(author)
                .build();

        ArticleEntity savedArticle = articleRepository.save(article);
        log.info("Article created successfully with id: {} by user: {}", savedArticle.getId(), username);

        return mapToDataResponse(savedArticle);
    }

    @Transactional(readOnly = true)
    public Page<ArticleDataDto> getAllArticles(Pageable pageable) {
        log.info("Retrieving all articles with pagination - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<ArticleEntity> articlesPage = articleRepository.findAll(pageable);
        return articlesPage.map(this::mapToDataResponse);
    }

    @Transactional(readOnly = true)
    public ArticleDataDto getArticleById(UUID id) {
        log.info("Retrieving article by ID: {}", id);
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + id));
        return mapToDataResponse(article);
    }

    @Transactional
    public ArticleDataDto updateArticle(UUID id, ArticleRequest articleRequest, String username) {
        log.info("Updating article with ID: {} by user: {}", id, username);

        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + id));

        if (!article.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to update this article");
        }

        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());

//        ArticleEntity updatedArticle = articleRepository.save(article);
        ArticleEntity updatedArticle = articleRepository.saveAndFlush(article);
        log.info("Article updated successfully with ID: {} by user: {}", updatedArticle.getId(), username);

        return mapToDataResponse(updatedArticle);
    }

    @Transactional
    public void deleteArticle(UUID id, String username) {
        log.info("Deleting article with ID: {} by user: {}", id, username);

        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + id));

        if (!article.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to delete this article");
        }

        articleRepository.delete(article);
        log.info("Article deleted successfully with ID: {} by user: {}", id, username);
    }

    @Transactional(readOnly = true)
    public Page<ArticleDataDto> getArticlesByAuthor(String username, Pageable pageable) {
        log.info("Retrieving articles by author: {}", username);
        Page<ArticleEntity> articlesPage = articleRepository.findByAuthorUsername(username, pageable);
        return articlesPage.map(this::mapToDataResponse);
    }

    private ArticleDataDto mapToDataResponse(ArticleEntity article) {
        return ArticleDataDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .authorUsername(article.getAuthorUsername())
                .authorProfilePicture(article.getAuthorProfilePicture())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .averageRating(article.getAverageRating())
                .totalRatings(article.getTotalRatings())
                .build();
    }

    public Page<ArticleDataDto> searchArticles(ArticleFilterDto filter, Pageable pageable) {
        GenericSpecificationBuilder<ArticleEntity> builder = new GenericSpecificationBuilder<>();
        Specification<ArticleEntity> spec = Specification.where(null);

        if (filter.getSearchTerm() != null) {
            spec = spec.and(builder.withTextSearch(
                    filter.getSearchTerm(),
                    "title", "content"
            ));
        }

        if (filter.getAuthorUsername() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("author").get("username"), filter.getAuthorUsername())
            );
        }

        return articleRepository.findAll(spec, pageable).map(this::mapToDataResponse);
    }

    @Transactional
    public int fixNullUpdatedAtTimestamps() {
        return articleRepository.updateNullUpdatedAtTimestamps();
    }

}
