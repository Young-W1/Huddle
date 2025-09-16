package com.capstone.huddle.comments.repository;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.comments.model.CommentsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentsRepository extends JpaRepository<CommentsEntity, UUID> {

    // Option 1: Use the article entity reference
    Page<CommentsEntity> findByArticle(ArticleEntity article, Pageable pageable);

    // Option 2: Use nested property access
    Page<CommentsEntity> findByArticle_Id(UUID articleId, Pageable pageable);

    // Option 3: Use JPQL query
    @Query("SELECT c FROM CommentsEntity c WHERE c.article.id = :articleId")
    List<CommentsEntity> findByArticleId(@Param("articleId") UUID articleId);

    // Additional useful methods
    List<CommentsEntity> findByAuthor_Username(String username);

    @Query("SELECT COUNT(c) FROM CommentsEntity c WHERE c.article.id = :articleId")
    Long countByArticleId(@Param("articleId") UUID articleId);
}
