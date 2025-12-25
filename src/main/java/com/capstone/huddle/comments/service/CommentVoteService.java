package com.capstone.huddle.comments.service;

import com.capstone.huddle.comments.enums.VoteType;
import com.capstone.huddle.comments.model.CommentVoteEntity;
import com.capstone.huddle.comments.model.CommentsEntity;
import com.capstone.huddle.comments.repository.CommentVoteRepository;
import com.capstone.huddle.comments.repository.CommentsRepository;
import com.capstone.huddle.notifications.service.NotificationService;
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
    private final NotificationService notificationService;

    @Transactional
    public CommentVoteEntity voteComment(UUID commentId, String username, VoteType voteType) {
        CommentsEntity comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Optional<CommentVoteEntity> existingVote = commentVoteRepository.findByCommentAndUser(comment, user);
        boolean isNewVote = false;
        CommentVoteEntity resultVote = null;

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
                resultVote = commentVoteRepository.save(vote);
                isNewVote = true; // Changed vote type, treat as new vote for notification
            }
        } else {
            // Create new vote
            CommentVoteEntity vote = CommentVoteEntity.builder()
                    .comment(comment)
                    .user(user)
                    .voteType(voteType)
                    .build();
            resultVote = commentVoteRepository.save(vote);
            isNewVote = true;
        }

        updateCommentVoteCounts(comment);

        // Send notification only for new votes (not when removing) and not to self
        if (isNewVote && !comment.getAuthor().getUsername().equals(username)) {
            notificationService.createCommentVoteNotification(
                    user,
                    comment.getAuthor(),
                    commentId,
                    voteType == VoteType.UPVOTE
            );
        }

        return resultVote;
    }

    private void updateCommentVoteCounts(CommentsEntity comment) {
        int upvotes = commentVoteRepository.countByCommentAndVoteType(comment, VoteType.UPVOTE);
        int downvotes = commentVoteRepository.countByCommentAndVoteType(comment, VoteType.DOWNVOTE);

        comment.setUpvotes(upvotes);
        comment.setDownvotes(downvotes);
        commentsRepository.save(comment);
    }
}
