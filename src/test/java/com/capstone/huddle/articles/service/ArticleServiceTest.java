package com.capstone.huddle.articles.service;

import com.capstone.huddle.articles.dto.request.ArticleDataDto;
import com.capstone.huddle.articles.dto.request.ArticleRequest;
import com.capstone.huddle.articles.dto.response.ArticleResponse;
import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.users.model.ProfileEntity;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ArticleService articleService;

    private UserEntity author;
    private ArticleEntity article;
    private UUID articleId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        articleId = UUID.randomUUID();

        ProfileEntity profile = ProfileEntity.builder()
                .bio("Author bio")
                .profilePicture("author.jpg")
                .build();

        author = UserEntity.builder()
                .id(userId)
                .username("author1")
                .email("author@example.com")
                .profile(profile)
                .createdAt(LocalDateTime.now())
                .build();

        article = ArticleEntity.builder()
                .id(articleId)
                .title("Test Article")
                .content("Test Content")
                .author(author)
                .createdAt(LocalDateTime.now())
                .averageRating(4.5)
                .totalRatings(10)
                .build();

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void userCanCreateArticle() {
        // Given
        ArticleRequest request = ArticleRequest.builder()
                .title("New Article")
                .content("Article Content")
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("author1", null);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findByUsername("author1")).thenReturn(Optional.of(author));
        when(articleRepository.save(any(ArticleEntity.class))).thenReturn(article);

        // When
        ArticleResponse<ArticleDataDto> response = articleService.createArticle(request);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Article created successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("author1", response.getData().getAuthorUsername());
        assertEquals("author.jpg", response.getData().getAuthorProfilePicture());
        verify(articleRepository).save(any(ArticleEntity.class));
    }

    @Test
    void cannotCreateArticleWithNonExistentUser() {
        // Given
        ArticleRequest request = ArticleRequest.builder()
                .title("New Article")
                .content("Content")
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("nonexistent", null);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                articleService.createArticle(request)
        );

        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(articleRepository, never()).save(any());
    }

    @Test
    void getAllArticlesIncludesAuthorInfo() {
        // Given
        List<ArticleEntity> articles = List.of(article);
        Pageable pageable = PageRequest.of(0, 10);
        when(articleRepository.findAll(pageable)).thenReturn(new PageImpl<>(articles));

        // When
        ArticleResponse<List<ArticleDataDto>> response = articleService.getAllArticles(pageable);

        // Then
        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        ArticleDataDto dto = response.getData().get(0);
        assertEquals("author1", dto.getAuthorUsername());
        assertEquals("author.jpg", dto.getAuthorProfilePicture());
        assertEquals(4.5, dto.getAverageRating());
        assertEquals(10, dto.getTotalRatings());
    }

    @Test
    void userCanUpdateOwnArticle() {
        // Given
        ArticleRequest updateRequest = ArticleRequest.builder()
                .title("Updated Title")
                .content("Updated Content")
                .build();

        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(article)).thenReturn(article);

        // When
        ArticleResponse<ArticleDataDto> response = articleService.updateArticle(articleId, updateRequest);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Article updated successfully", response.getMessage());
        assertEquals("Updated Title", article.getTitle());
        assertEquals("Updated Content", article.getContent());
        verify(articleRepository).save(article);
    }

    @Test
    void cannotUpdateNonExistentArticle() {
        // Given
        ArticleRequest updateRequest = ArticleRequest.builder()
                .title("Updated")
                .content("Content")
                .build();

        when(articleRepository.findById(articleId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                articleService.updateArticle(articleId, updateRequest)
        );

        assertTrue(exception.getMessage().contains("Article not found"));
        verify(articleRepository, never()).save(any());
    }

    @Test
    void userCanDeleteArticle() {
        // Given
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // When
        ArticleResponse<Void> response = articleService.deleteArticle(articleId);

        // Then
        assertTrue(response.isSuccess());
        assertEquals("Article deleted successfully", response.getMessage());
        verify(articleRepository).delete(article);
    }

    @Test
    void getArticleByIdIncludesAuthorInfo() {
        // Given
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // When
        ArticleResponse<ArticleDataDto> response = articleService.getArticleById(articleId);

        // Then
        assertTrue(response.isSuccess());
        ArticleDataDto dto = response.getData();
        assertEquals("author1", dto.getAuthorUsername());
        assertEquals("author.jpg", dto.getAuthorProfilePicture());
        assertEquals("Test Article", dto.getTitle());
    }

    @Test
    void articleWithoutProfileReturnsNullProfilePicture() {
        // Given
        author.setProfile(null);
        when(articleRepository.findById(articleId)).thenReturn(Optional.of(article));

        // When
        ArticleResponse<ArticleDataDto> response = articleService.getArticleById(articleId);

        // Then
        assertTrue(response.isSuccess());
        ArticleDataDto dto = response.getData();
        assertEquals("author1", dto.getAuthorUsername());
        assertNull(dto.getAuthorProfilePicture());
    }
}