package com.capstone.huddle.articles.service;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.model.ArticleRatingEntity;
import com.capstone.huddle.articles.repository.ArticleRatingRepository;
import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleRatingServiceTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ArticleRatingRepository ratingRepository;
    @InjectMocks
    private ArticleRatingService articleRatingService;

    // Java
    @Test
    void userCanRateArticleAndAverageIsUpdated() {
        UUID articleId = UUID.randomUUID();
        String username = "user1";
        ArticleEntity article = new ArticleEntity();
        UserEntity user = new UserEntity();

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(ratingRepository.findByArticleAndUser(article, user)).thenReturn(Optional.empty());
        when(ratingRepository.save(ArgumentMatchers.any(ArticleRatingEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(ratingRepository.findByArticle(article)).thenReturn(List.of(
                ArticleRatingEntity.builder()
                        .article(article)
                        .user(user)
                        .rating(4)
                        .createdAt(LocalDateTime.now())
                        .build()
        ));

        ArticleRatingEntity result = articleRatingService.rateArticle(articleId, username, 4);

        assertNotNull(result);
        assertEquals(4, result.getRating());
        verify(ratingRepository).save(ArgumentMatchers.any(ArticleRatingEntity.class));
        verify(articleRepository).save(article);
        assertEquals(4.0, article.getAverageRating());
        assertEquals(1, article.getTotalRatings());
    }

    @Test
    void userCanUpdateRating() {
        UUID articleId = UUID.randomUUID();
        String username = "user1";
        ArticleEntity article = new ArticleEntity();
        UserEntity user = new UserEntity();
        ArticleRatingEntity existingRating = ArticleRatingEntity.builder()
                .article(article)
                .user(user)
                .rating(3)
                .createdAt(LocalDateTime.now())
                .build();

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(ratingRepository.findByArticleAndUser(article, user)).thenReturn(Optional.of(existingRating));
        when(ratingRepository.save(ArgumentMatchers.any(ArticleRatingEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(ratingRepository.findByArticle(article)).thenReturn(List.of(existingRating));

        ArticleRatingEntity result = articleRatingService.rateArticle(articleId, username, 5);

        assertNotNull(result);
        assertEquals(5, existingRating.getRating());
        verify(ratingRepository).save(existingRating);
        verify(articleRepository).save(article);
    }

}