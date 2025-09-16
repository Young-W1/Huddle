package com.capstone.huddle.comments.model;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.users.model.UserEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "comments")
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"article", "author", "votes"})
@EqualsAndHashCode(exclude = {"article", "author", "votes"})
public class CommentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @JsonBackReference
    private ArticleEntity article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_username", referencedColumnName = "username", nullable = false)
    @JsonIgnore
    private UserEntity author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    @Builder.Default
    private Integer upvotes = 0;

    @Column
    @Builder.Default
    private Integer downvotes = 0;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<CommentVoteEntity> votes = new ArrayList<>();

    // Transient field to expose author username without circular reference
    @Transient
    public String getAuthorUsername() {
        return author != null ? author.getUsername() : null;
    }

    @Transient
    public String getAuthorName() {
        return author != null ? author.getFirstName() + " " + author.getLastName() : null;
    }

    @Transient
    public UUID getArticleId() {
        return article != null ? article.getId() : null;
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
