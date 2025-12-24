package com.capstone.huddle.articles.repository;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.model.ArticleStatus;
import com.capstone.huddle.users.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, UUID>, JpaSpecificationExecutor<ArticleEntity> {

    Optional<ArticleEntity> findById(java.util.UUID id);

    // Find articles by author
    List<ArticleEntity> findByAuthor(UserEntity author);

    // Find articles by author username - returns a list (no pagination)
    @Query("SELECT a FROM ArticleEntity a WHERE a.author.username = :username")
    List<ArticleEntity> findByAuthorUsername(@Param("username") String username);

    // Find articles by author username with pagination
    @Query("SELECT a FROM ArticleEntity a WHERE a.author.username = :username")
    Page<ArticleEntity> findByAuthorUsername(@Param("username") String username, Pageable pageable);

    // Find articles by title containing (case-insensitive)
    List<ArticleEntity> findByTitleContainingIgnoreCase(String title);

    // Find articles created between dates
    List<ArticleEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find latest articles with pagination
    Page<ArticleEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Find article by id and author (for authorization check)
    Optional<ArticleEntity> findByIdAndAuthor(UUID id, UserEntity author);

    // Search articles by title or content
    @Query("SELECT a FROM ArticleEntity a WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<ArticleEntity> searchArticles(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Count articles by author
    Long countByAuthor(UserEntity author);

    @Modifying
    @Query(value = "UPDATE articles SET updated_at = created_at WHERE updated_at IS NULL", nativeQuery = true)
    int updateNullUpdatedAtTimestamps();

    @Query("SELECT COUNT(a) FROM ArticleEntity a")
    long countTotalArticles();

    @Query("SELECT COUNT(a) FROM ArticleEntity a WHERE a.createdAt >= :date")
    long countArticlesCreatedAfter(@Param("date") LocalDateTime date);

    @Query("SELECT a.category, COUNT(a) FROM ArticleEntity a GROUP BY a.category")
    List<Object[]> countArticlesByCategory();

    @Query("SELECT a.author.username, COUNT(a) as postCount FROM ArticleEntity a GROUP BY a.author.username ORDER BY postCount DESC")
    List<Object[]> getTopContributors(Pageable pageable);

    // Draft-related queries
    @Query("SELECT a FROM ArticleEntity a WHERE a.author.username = :username AND a.status = :status ORDER BY a.updatedAt DESC")
    Page<ArticleEntity> findByAuthorUsernameAndStatus(@Param("username") String username, @Param("status") ArticleStatus status, Pageable pageable);

    @Query("SELECT a FROM ArticleEntity a WHERE a.status = 'PUBLISHED' ORDER BY a.createdAt DESC")
    Page<ArticleEntity> findPublishedArticles(Pageable pageable);

    @Query("SELECT a FROM ArticleEntity a WHERE a.status = 'PUBLISHED' AND " +
            "(LOWER(a.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<ArticleEntity> searchPublishedArticles(@Param("searchTerm") String searchTerm, Pageable pageable);

    long countByAuthorAndStatus(UserEntity author, ArticleStatus status);
}
