package com.capstone.huddle.comments.service;

import com.capstone.huddle.comments.enums.VoteType;
import com.capstone.huddle.comments.model.CommentVoteEntity;
import com.capstone.huddle.comments.model.CommentsEntity;
import com.capstone.huddle.comments.repository.CommentVoteRepository;
import com.capstone.huddle.comments.repository.CommentsRepository;
import com.capstone.huddle.users.model.UserEntity;
import com.capstone.huddle.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentVoteServiceTest {

    @Mock
    private CommentVoteRepository commentVoteRepository;
    @Mock
    private CommentsRepository commentsRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentVoteService commentVoteService;

    @Test
    void testVoteOnComment_NewVote_Upvote() {
        UUID commentId = UUID.randomUUID();
        String username = "testUser";
        VoteType voteType = VoteType.UPVOTE;

        CommentsEntity comment = new CommentsEntity();
        UserEntity user = new UserEntity();

        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(commentVoteRepository.findByCommentAndUser(comment, user)).thenReturn(Optional.empty());
        when(commentVoteRepository.save(any(CommentVoteEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(commentVoteRepository.countByCommentAndVoteType(comment, VoteType.UPVOTE)).thenReturn(1);
        when(commentVoteRepository.countByCommentAndVoteType(comment, VoteType.DOWNVOTE)).thenReturn(0);

        CommentVoteEntity result = commentVoteService.voteComment(commentId, username, voteType);

        assertNotNull(result);
        assertEquals(voteType, result.getVoteType());
        verify(commentVoteRepository).save(any(CommentVoteEntity.class));
        verify(commentsRepository).save(comment);
    }

    @Test
    void testVoteOnComment_ExistingVote_SameType_RemovesVote() {
        UUID commentId = UUID.randomUUID();
        String username = "testUser";
        VoteType voteType = VoteType.UPVOTE;

        CommentsEntity comment = new CommentsEntity();
        UserEntity user = new UserEntity();
        CommentVoteEntity existingVote = CommentVoteEntity.builder()
                .comment(comment)
                .user(user)
                .voteType(voteType)
                .build();

        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(commentVoteRepository.findByCommentAndUser(comment, user)).thenReturn(Optional.of(existingVote));
        when(commentVoteRepository.countByCommentAndVoteType(comment, VoteType.UPVOTE)).thenReturn(0);
        when(commentVoteRepository.countByCommentAndVoteType(comment, VoteType.DOWNVOTE)).thenReturn(0);

        CommentVoteEntity result = commentVoteService.voteComment(commentId, username, voteType);

        assertNull(result);
        verify(commentVoteRepository).delete(existingVote);
        verify(commentsRepository).save(comment);
    }

    @Test
    void testVoteOnComment_ExistingVote_ChangeType() {
        UUID commentId = UUID.randomUUID();
        String username = "testUser";
        VoteType oldVoteType = VoteType.UPVOTE;
        VoteType newVoteType = VoteType.DOWNVOTE;

        CommentsEntity comment = new CommentsEntity();
        UserEntity user = new UserEntity();
        CommentVoteEntity existingVote = CommentVoteEntity.builder()
                .comment(comment)
                .user(user)
                .voteType(oldVoteType)
                .build();

        when(commentsRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(commentVoteRepository.findByCommentAndUser(comment, user)).thenReturn(Optional.of(existingVote));
        when(commentVoteRepository.save(any(CommentVoteEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(commentVoteRepository.countByCommentAndVoteType(comment, VoteType.UPVOTE)).thenReturn(0);
        when(commentVoteRepository.countByCommentAndVoteType(comment, VoteType.DOWNVOTE)).thenReturn(1);

        CommentVoteEntity result = commentVoteService.voteComment(commentId, username, newVoteType);

        assertNotNull(result);
        assertEquals(newVoteType, result.getVoteType());
        verify(commentVoteRepository).save(existingVote);
        verify(commentsRepository).save(comment);
    }

}