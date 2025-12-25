package com.capstone.huddle.articles.service;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.model.ArticleRatingEntity;
import com.capstone.huddle.articles.repository.ArticleRatingRepository;
import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleRatingService {

    private final ArticleRatingRepository articleRatingRepository;
    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;

    @Transactional
    public ArticleRatingEntity rateArticle(UUID articleId, String username, Integer rating) {
        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with ID: " + articleId));
            UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));

        ArticleRatingEntity articleRating = articleRatingRepository.findByArticleAndUser(article, user)
                .orElse(ArticleRatingEntity.builder()
                        .article(article)
                        .user(user)
                        .build());

        articleRating.setRating(rating);
        articleRating = articleRatingRepository.save(articleRating);

        updateArticleAverageRating(article);
        return articleRating;

    }

    private void updateArticleAverageRating(ArticleEntity article) {
        List<ArticleRatingEntity> ratings = articleRatingRepository.findByArticle(article);
        double average = ratings.stream()
                .mapToInt(ArticleRatingEntity::getRating)
                .average()
                .orElse(0.0);

        article.setAverageRating(average);
        article.setTotalRatings(ratings.size());
        articleRepository.save(article);
    }
}
