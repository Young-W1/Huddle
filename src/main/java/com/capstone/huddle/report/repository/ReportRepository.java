package com.capstone.huddle.report.repository;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.report.entity.ReportEntity;
import com.capstone.huddle.report.entity.ReportStatus;
import com.capstone.huddle.users.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {

    Page<ReportEntity> findByStatus(ReportStatus status, Pageable pageable);

    Page<ReportEntity> findByReporter(UserEntity reporter, Pageable pageable);

    Page<ReportEntity> findByReportedUser(UserEntity reportedUser, Pageable pageable);

    Page<ReportEntity> findByArticle(ArticleEntity article, Pageable pageable);

    boolean existsByReporterAndArticle(UserEntity reporter, ArticleEntity article);

    long countByStatus(ReportStatus status);

    @Query("SELECT r FROM ReportEntity r WHERE r.status = :status ORDER BY r.createdAt DESC")
    Page<ReportEntity> findRecentByStatus(@Param("status") ReportStatus status, Pageable pageable);
}
