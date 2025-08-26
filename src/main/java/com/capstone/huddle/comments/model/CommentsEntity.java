package com.capstone.huddle.comments.model;

import com.capstone.huddle.articles.model.ArticleEntity;
import com.capstone.huddle.users.model.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

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
public class CommentsEntity {

    //Fields: id, article (ManyToOne), author (ManyToOne to User), body, createdAt, updatedAt

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ArticleEntity article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_username", referencedColumnName = "username", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UserEntity author;

    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Column
    private Integer upvotes = 0;

    @Column
    private Integer downvotes = 0;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentVoteEntity> votes = new ArrayList<>();

}
