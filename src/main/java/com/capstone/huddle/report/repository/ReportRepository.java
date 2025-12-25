package com.capstone.huddle.report.repository;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.report.entity.ReportEntity;
import com.capstone.huddle.report.entity.ReportStatus;
import com.capstone.huddle.users.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, UUID> {
    Page<ReportEntity> findByStatus(ReportStatus status, Pageable pageable);
    Page<ReportEntity> findByReporterId(UUID reporterId, Pageable pageable);
    Page<ReportEntity> findByReportedArticleId(UUID articleId, Pageable pageable);
    Page<ReportEntity> findByReporter(UserEntity reporter, Pageable pageable);

    boolean existsByReporterAndReportedArticle(UserEntity reporter, ArticleEntity article);

    @Modifying
    void deleteByReportedArticle(ArticleEntity article);

    @Query("SELECT r.status, COUNT(r) FROM ReportEntity r GROUP BY r.status")
    List<Object[]> countReportsByStatus();

    long countByStatus(ReportStatus status);

}
