package com.capstone.huddle.articles.service;

import com.capstone.huddle.articles.dto.request.ArticleDataDto;
import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.model.BookmarkEntity;
import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.articles.repository.BookmarkRepository;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookmarkEntity addBookmark(String username, UUID articleId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + articleId));

        // Check if bookmark already exists
        if (bookmarkRepository.existsByUserAndArticle(user, article)) {
            throw new RuntimeException("Article already bookmarked");
        }

        BookmarkEntity bookmark = BookmarkEntity.builder()
                .user(user)
                .article(article)
                .build();

        log.info("User {} bookmarked article {}", username, articleId);
        return bookmarkRepository.save(bookmark);
    }

    @Transactional
    public void removeBookmark(String username, UUID articleId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + articleId));

        bookmarkRepository.deleteByUserAndArticle(user, article);
        log.info("User {} removed bookmark for article {}", username, articleId);
    }

    @Transactional(readOnly = true)
    public boolean isBookmarked(String username, UUID articleId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + articleId));

        return bookmarkRepository.existsByUserAndArticle(user, article);
    }

    @Transactional(readOnly = true)
    public Page<ArticleDataDto> getUserBookmarks(String username, Pageable pageable) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return bookmarkRepository.findBookmarkedArticlesByUser(user, pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public long getBookmarkCount(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return bookmarkRepository.countByUser(user);
    }

    private ArticleDataDto mapToDto(ArticleEntity article) {
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
                .authorId(article.getAuthorId())
                .build();
    }
}

