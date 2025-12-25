package com.capstone.huddle.articles.service;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.articles.model.ArticleShareEntity;
import com.capstone.huddle.articles.repository.ArticleRepository;
import com.capstone.huddle.articles.repository.ArticleShareRepository;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleShareService {

    private final ArticleShareRepository shareRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Value("${app.base-url:http://localhost:3000}")
    private String baseUrl;

    @Transactional
    public Map<String, String> generateShareLinks(UUID articleId, String username) {
        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + articleId));

        String articleUrl = baseUrl + "/articles/" + articleId;
        String encodedUrl = URLEncoder.encode(articleUrl, StandardCharsets.UTF_8);
        String encodedTitle = URLEncoder.encode(article.getTitle(), StandardCharsets.UTF_8);

        Map<String, String> shareLinks = new HashMap<>();

        // Twitter/X
        shareLinks.put("twitter", String.format(
                "https://twitter.com/intent/tweet?url=%s&text=%s",
                encodedUrl, encodedTitle));

        // Facebook
        shareLinks.put("facebook", String.format(
                "https://www.facebook.com/sharer/sharer.php?u=%s",
                encodedUrl));

        // LinkedIn
        shareLinks.put("linkedin", String.format(
                "https://www.linkedin.com/sharing/share-offsite/?url=%s",
                encodedUrl));

        // Email
        shareLinks.put("email", String.format(
                "mailto:?subject=%s&body=Check out this article: %s",
                encodedTitle, articleUrl));

        // Direct link
        shareLinks.put("directLink", articleUrl);

        return shareLinks;
    }

    @Transactional
    public ArticleShareEntity recordShare(UUID articleId, String username, ArticleShareEntity.SharePlatform platform) {
        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + articleId));

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        String shareToken = UUID.randomUUID().toString();

        ArticleShareEntity share = ArticleShareEntity.builder()
                .article(article)
                .sharedBy(user)
                .platform(platform)
                .shareToken(shareToken)
                .build();

        log.info("User {} shared article {} on {}", username, articleId, platform);
        return shareRepository.save(share);
    }

    public long getShareCount(UUID articleId) {
        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + articleId));

        return shareRepository.countByArticle(article);
    }

    public Map<String, Long> getShareCountByPlatform(UUID articleId) {
        ArticleEntity article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found: " + articleId));

        Map<String, Long> platformCounts = new HashMap<>();
        shareRepository.countSharesByPlatform(article).forEach(row -> {
            platformCounts.put(((ArticleShareEntity.SharePlatform) row[0]).name(), (Long) row[1]);
        });

        return platformCounts;
    }
}

