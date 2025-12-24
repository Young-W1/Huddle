package com.capstone.huddle.articles.service;

import com.capstone.huddle.articles.dto.ArticleFilterDto;
import com.capstone.huddle.articles.dto.request.ArticleDataDto;
import com.capstone.huddle.articles.dto.request.ArticleRequest;
import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.model.ArticleStatus;
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

        // Default to PUBLISHED if status not specified
        ArticleStatus status = articleRequest.getStatus() != null
                ? articleRequest.getStatus()
                : ArticleStatus.PUBLISHED;

        ArticleEntity article = ArticleEntity.builder()
                .title(articleRequest.getTitle())
                .content(articleRequest.getContent())
                .author(author)
                .status(status)
                .build();

        ArticleEntity savedArticle = articleRepository.save(article);
        log.info("Article created successfully with id: {} status: {} by user: {}",
                savedArticle.getId(), status, username);

        return mapToDataResponse(savedArticle);
    }

    @Transactional(readOnly = true)
    public Page<ArticleDataDto> getAllArticles(Pageable pageable) {
        log.info("Retrieving all published articles with pagination - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        // Only return published articles, not drafts
        Page<ArticleEntity> articlesPage = articleRepository.findPublishedArticles(pageable);
        return articlesPage.map(this::mapToDataResponse);
    }

    @Transactional(readOnly = true)
    public ArticleDataDto getArticleById(UUID id) {
        log.info("Retrieving article by ID: {}", id);
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + id));
        return mapToDataResponse(article);
    }

    @Transactional(readOnly = true)
    public ArticleDataDto getArticleById(UUID id, String currentUsername) {
        log.info("Retrieving article by ID: {} for user: {}", id, currentUsername);
        ArticleEntity article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with ID: " + id));

        // If the article is a draft, only the author will be able to see it
        if (article.getStatus() == ArticleStatus.DRAFT) {
            if (currentUsername == null || !article.getAuthor().getUsername().equals(currentUsername)) {
                throw new RuntimeException("Article not found with ID: " + id);
            }
        }

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
        log.info("Retrieving published articles by author: {}", username);
        // Only return published articles for the author - drafts are shown separately
        Page<ArticleEntity> articlesPage = articleRepository.findByAuthorUsernameAndStatus(username, ArticleStatus.PUBLISHED, pageable);
        return articlesPage.map(this::mapToDataResponse);
    }

    @Transactional(readOnly = true)
    public Page<ArticleDataDto> getDraftsByAuthor(String username, Pageable pageable) {
        log.info("Retrieving draft articles by author: {}", username);
        Page<ArticleEntity> draftsPage = articleRepository.findByAuthorUsernameAndStatus(username, ArticleStatus.DRAFT, pageable);
        return draftsPage.map(this::mapToDataResponse);
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
                .authorId(article.getAuthor().getId())
                .build();
    }

    public Page<ArticleDataDto> searchArticles(ArticleFilterDto filter, Pageable pageable) {
        GenericSpecificationBuilder<ArticleEntity> builder = new GenericSpecificationBuilder<>();
        Specification<ArticleEntity> spec = Specification.where(null);

        // Always filter to only show published articles (not drafts)
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), ArticleStatus.PUBLISHED)
        );

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
