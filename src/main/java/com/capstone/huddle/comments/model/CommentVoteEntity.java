package com.capstone.huddle.comments.model;

import com.capstone.huddle.comments.enums.VoteType;
import com.capstone.huddle.users.model.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comment_votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"comment_id", "user_id"}))
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentVoteEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private java.util.UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentsEntity comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VoteType voteType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
