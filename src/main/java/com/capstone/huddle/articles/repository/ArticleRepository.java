package com.capstone.huddle.articles.repository;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.users.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {

    Optional <ArticleEntity> findById(Long id);

    // Find articles by author
    List<ArticleEntity> findByAuthor(UserEntity author);

    // Find articles by author username
    @Query("SELECT a FROM ArticleEntity a WHERE a.author.username = :username")
    List<ArticleEntity> findByAuthorUsername(@Param("username") String username);

    // Find articles by title containing (case-insensitive)
    List<ArticleEntity> findByTitleContainingIgnoreCase(String title);

    // Find articles created between dates
    List<ArticleEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find latest articles with pagination
    Page<ArticleEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Find article by id and author (for authorization check)
    Optional<ArticleEntity> findByIdAndAuthor(Long id, UserEntity author);

    // Search articles by title or content
    @Query("SELECT a FROM ArticleEntity a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ArticleEntity> searchArticles(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Count articles by author
    Long countByAuthor(UserEntity author);
}
