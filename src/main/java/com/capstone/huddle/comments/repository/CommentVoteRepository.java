package com.capstone.huddle.comments.repository;

import com.capstone.huddle.comments.enums.VoteType;
import com.capstone.huddle.comments.model.CommentVoteEntity;
import com.capstone.huddle.comments.model.CommentsEntity;
import com.capstone.huddle.users.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVoteEntity, UUID> {

    Optional<CommentVoteEntity> findByCommentAndUser(CommentsEntity comment, UserEntity user);
    List<CommentVoteEntity> findByComment(CommentsEntity comment);
    Integer countByCommentAndVoteType(CommentsEntity comment, VoteType voteType);
    void deleteByComment(CommentsEntity comment);

}
