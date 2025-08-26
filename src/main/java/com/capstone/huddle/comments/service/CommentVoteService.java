package com.capstone.huddle.comments.service;

import com.capstone.huddle.comments.enums.VoteType;
import com.capstone.huddle.comments.model.CommentVoteEntity;
import com.capstone.huddle.comments.model.CommentsEntity;
import com.capstone.huddle.comments.repository.CommentVoteRepository;
import com.capstone.huddle.comments.repository.CommentsRepository;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentVoteService {

    private final CommentVoteRepository commentVoteRepository;
    private final CommentsRepository commentsRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentVoteEntity voteComment(UUID commentId, String username, VoteType voteType) {
        CommentsEntity comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Optional<CommentVoteEntity> existingVote = commentVoteRepository.findByCommentAndUser(comment, user);

        if (existingVote.isPresent()) {
            CommentVoteEntity vote = existingVote.get();
            if (vote.getVoteType() == voteType) {
                // Remove vote if same type
                commentVoteRepository.delete(vote);
                updateCommentVoteCounts(comment);
                return null;
            } else {
                // Change vote type
                vote.setVoteType(voteType);
                vote = commentVoteRepository.save(vote);
                updateCommentVoteCounts(comment);
                return vote;
            }
        } else {
            // Create new vote
            CommentVoteEntity vote = CommentVoteEntity.builder()
                    .comment(comment)
                    .user(user)
                    .voteType(voteType)
                    .build();
            vote = commentVoteRepository.save(vote);
            updateCommentVoteCounts(comment);
            return vote;
        }
    }

    private void updateCommentVoteCounts(CommentsEntity comment) {
        int upvotes = commentVoteRepository.countByCommentAndVoteType(comment, VoteType.UPVOTE);
        int downvotes = commentVoteRepository.countByCommentAndVoteType(comment, VoteType.DOWNVOTE);

        comment.setUpvotes(upvotes);
        comment.setDownvotes(downvotes);
        commentsRepository.save(comment);
    }
}
