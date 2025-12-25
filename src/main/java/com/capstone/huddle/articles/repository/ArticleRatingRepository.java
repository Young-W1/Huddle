package com.capstone.huddle.articles.repository;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.model.ArticleRatingEntity;
import com.capstone.huddle.users.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRatingRepository extends JpaRepository<ArticleRatingEntity, UUID> {

    Optional<ArticleRatingEntity> findByArticleAndUser(ArticleEntity article, UserEntity user);
    List<ArticleRatingEntity> findByArticle(ArticleEntity article);
    Double findAverageRatingByArticleId(UUID articleId);
    void deleteByArticle(ArticleEntity article);

}
