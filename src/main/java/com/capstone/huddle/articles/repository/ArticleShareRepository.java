package com.capstone.huddle.articles.repository;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.model.ArticleShareEntity;
import com.capstone.huddle.users.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleShareRepository extends JpaRepository<ArticleShareEntity, UUID> {

    Optional<ArticleShareEntity> findByShareToken(String shareToken);

    long countByArticle(ArticleEntity article);

    List<ArticleShareEntity> findByArticle(ArticleEntity article);

    Page<ArticleShareEntity> findBySharedByOrderBySharedAtDesc(UserEntity user, Pageable pageable);

    @Query("SELECT s.platform, COUNT(s) FROM ArticleShareEntity s WHERE s.article = :article GROUP BY s.platform")
    List<Object[]> countSharesByPlatform(@Param("article") ArticleEntity article);

    @Query("SELECT s.article.id, COUNT(s) as shareCount FROM ArticleShareEntity s GROUP BY s.article.id ORDER BY shareCount DESC")
    List<Object[]> getMostSharedArticles(Pageable pageable);
}

