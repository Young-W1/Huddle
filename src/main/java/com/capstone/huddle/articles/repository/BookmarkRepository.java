package com.capstone.huddle.articles.repository;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.model.BookmarkEntity;
import com.capstone.huddle.users.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, UUID> {

    Optional<BookmarkEntity> findByUserAndArticle(UserEntity user, ArticleEntity article);

    boolean existsByUserAndArticle(UserEntity user, ArticleEntity article);

    Page<BookmarkEntity> findByUserOrderByCreatedAtDesc(UserEntity user, Pageable pageable);

    @Query("SELECT b.article FROM BookmarkEntity b JOIN FETCH b.article.author LEFT JOIN FETCH b.article.author.profile WHERE b.user = :user ORDER BY b.createdAt DESC")
    Page<ArticleEntity> findBookmarkedArticlesByUser(@Param("user") UserEntity user, Pageable pageable);

    long countByUser(UserEntity user);

    void deleteByUserAndArticle(UserEntity user, ArticleEntity article);

    void deleteByArticle(ArticleEntity article);
}

