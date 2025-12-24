package com.capstone.huddle.articles.model;

import com.capstone.huddle.users.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "article_shares")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArticleShareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private ArticleEntity article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_by_user_id", nullable = false)
    private UserEntity sharedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SharePlatform platform;

    @Column(unique = true)
    private String shareToken;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime sharedAt;

    public enum SharePlatform {
        TWITTER,
        FACEBOOK,
        LINKEDIN,
        EMAIL,
        COPY_LINK,
        OTHER
    }
}

