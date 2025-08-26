package com.capstone.huddle.comments.repository;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.comments.model.CommentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentsRepository extends JpaRepository <CommentsEntity, UUID> {

    List<CommentsEntity> findByArticle(ArticleEntity article);

    // Alternative: find by article ID directly
    List<CommentsEntity> findByArticleId(UUID articleId);

    // Optional: find comments ordered by creation date
    List<CommentsEntity> findByArticleOrderByCreatedAtDesc(ArticleEntity article);



}
