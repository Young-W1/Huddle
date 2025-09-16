package com.capstone.huddle.comments.service;

import com.capstone.huddle.articles.dto.response.ArticleResponse;
import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.comments.dto.response.CommentResponseDto;
import com.capstone.huddle.comments.dto.response.CommentsResponse;
import com.capstone.huddle.comments.model.CommentsEntity;
import com.capstone.huddle.comments.repository.CommentsRepository;
import com.capstone.huddle.notifications.service.NotificationService;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public CommentResponseDto addComment(UUID articleId, String authorUsername, String body) {
        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article not found"));
        UserEntity author = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        CommentsEntity comment = CommentsEntity.builder()
                .article(article)
                .author(author)
                .body(body)
                .build();

        CommentsEntity savedComment = commentsRepository.save(comment);

        // Send notification to article author if commenter is not the article author
        if (!article.getAuthor().getUsername().equals(authorUsername)) {
            notificationService.createNewCommentNotification(
                    author,
                    article.getAuthor(),
                    articleId,
                    article.getTitle()
            );
        }

        log.info("Comment added successfully to article {} by user {}", articleId, authorUsername);

        return mapToDto(savedComment);
    }

    @Transactional(readOnly = true)
    public ArticleResponse<Page<CommentResponseDto>> getCommentsByArticleId(UUID articleId, Pageable pageable) {
        log.info("Retrieving comments for article ID: {} with page: {}, size: {}",
                articleId, pageable.getPageNumber(), pageable.getPageSize());

        try {
            ArticleEntity article = articleRepository.findById(articleId)
                    .orElseThrow(() -> new EntityNotFoundException("Article not found with ID: " + articleId));

            Page<CommentsEntity> commentsPage = commentsRepository.findByArticle(article, pageable);

            // Convert to DTOs to avoid circular references
            Page<CommentResponseDto> commentDtos = commentsPage.map(this::mapToDto);

            log.info("Retrieved {} comments out of {} total for article ID: {}",
                    commentDtos.getNumberOfElements(), commentDtos.getTotalElements(), articleId);

            return ArticleResponse.<Page<CommentResponseDto>>builder()
                    .success(true)
                    .message("Comments retrieved successfully")
                    .data(commentDtos)
                    .build();
        } catch (EntityNotFoundException e) {
            log.error("Article not found: ", e);
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving comments: ", e);
            throw new RuntimeException("Failed to retrieve comments: " + e.getMessage());
        }
    }

    @Transactional
    public CommentsResponse<CommentResponseDto> updateComment(UUID commentId, String username, String newBody) {
        log.info("Updating comment {} by user {}", commentId, username);

        try {
            CommentsEntity comment = commentsRepository.findById(commentId)
                    .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

            // Check ownership
            if (!comment.getAuthor().getUsername().equals(username)) {
                log.error("User {} attempted to update comment {} owned by {}",
                        username, commentId, comment.getAuthor().getUsername());
                throw new SecurityException("You are not authorized to update this comment");
            }

            // Update the comment
            comment.setBody(newBody);
            comment.setUpdatedAt(LocalDateTime.now());

            CommentsEntity updatedComment = commentsRepository.save(comment);
            log.info("Successfully updated comment {}", commentId);

            return CommentsResponse.<CommentResponseDto>builder()
                    .success(true)
                    .message("Comment updated successfully")
                    .data(mapToDto(updatedComment))
                    .build();
        } catch (EntityNotFoundException e) {
            log.error("Comment not found: ", e);
            throw e;
        } catch (SecurityException e) {
            log.error("Unauthorized update attempt: ", e);
            throw e;
        } catch (Exception e) {
            log.error("Error updating comment: ", e);
            throw new RuntimeException("Failed to update comment: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteComment(UUID commentId, String username) {
        log.info("Deleting comment {} by user {}", commentId, username);
        CommentsEntity comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with ID: " + commentId));

        // Check ownership
        if (!comment.getAuthor().getUsername().equals(username)) {
            log.error("User {} attempted to delete comment {} owned by {}",
                    username, commentId, comment.getAuthor().getUsername());
            throw new SecurityException("You are not authorized to delete this comment");
        }

        commentsRepository.delete(comment);
        log.info("Successfully deleted comment {}", commentId);
    }

    // method to convert entity to DTO
    private CommentResponseDto mapToDto(CommentsEntity comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .body(comment.getBody())
                .authorUsername(comment.getAuthor().getUsername())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .articleId(comment.getArticle().getId())
                .articleTitle(comment.getArticle().getTitle())
                .build();
    }
}
