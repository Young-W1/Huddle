package com.capstone.huddle.articles.model;

import com.capstone.huddle.comments.model.CommentsEntity;
import com.capstone.huddle.users.model.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "articles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = {"author", "comments", "ratings"})
@EqualsAndHashCode(exclude = {"author", "comments", "ratings"})
public class ArticleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 100)
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserEntity author;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private Double averageRating = 0.0;

    @Column
    private Integer totalRatings = 0;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("article-ratings")
    @Builder.Default
    private List<ArticleRatingEntity> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("article-comments")
    @Builder.Default
    private List<CommentsEntity> comments = new ArrayList<>();

    // Transient fields for safe data exposure
    @Transient
    public String getAuthorUsername() {
        return author != null ? author.getUsername() : null;
    }

    @Transient
    public String getAuthorName() {
        return author != null ? author.getFirstName() + " " + author.getLastName() : null;
    }

    @Transient
    public UUID getAuthorId() {
        return author != null ? author.getId() : null;
    }

    @Transient
    public String getAuthorProfilePicture() {
        return author != null && author.getProfile() != null
                ? author.getProfile().getProfilePicture()
                : null;
    }

    @Transient
    public Integer getCommentCount() {
        return comments != null ? comments.size() : 0;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();  // Add this line

    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        System.out.println("PreUpdate called - setting updatedAt to: " + updatedAt);
    }
}
