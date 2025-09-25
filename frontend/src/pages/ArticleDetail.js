import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Container,
    Paper,
    Typography,
    Box,
    Chip,
    IconButton,
    Button,
    TextField,
    Divider,
    Avatar,
    List,
    ListItem,
    ListItemAvatar,
    ListItemText,
    CircularProgress,
    Alert,
    Card,
    CardContent,
    Stack,
    useTheme,
    alpha,
    Skeleton,
    Badge
} from '@mui/material';
import {
    ArrowBack,
    Person,
    Delete,
    Edit,
    Share,
    AccessTime,
    Comment as CommentIcon,
    ThumbUp,
    ThumbDown,
    Check,
    Close
} from '@mui/icons-material';
import axios from 'axios';

function ArticleDetail() {
    const { articleId } = useParams();
    const navigate = useNavigate();
    const theme = useTheme();
    const [article, setArticle] = useState(null);
    const [comments, setComments] = useState([]);
    const [userVotes, setUserVotes] = useState({}); // Track user's votes
    const [newComment, setNewComment] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [editingCommentId, setEditingCommentId] = useState(null);
    const [editingCommentText, setEditingCommentText] = useState('');
    const currentUsername = localStorage.getItem('username');

    useEffect(() => {
        fetchArticle();
        fetchComments();
    }, [articleId]);

    const fetchArticle = async () => {
        try {
            const response = await axios.get(`/huddle/articles/article/${articleId}`);
            if (response.data.success) {
                setArticle(response.data.data);
            }
            setLoading(false);
        } catch (err) {
            console.error('Failed to load article:', err);
            setError('Failed to load article');
            setLoading(false);
        }
    };

    const fetchComments = async () => {
        try {
            const response = await axios.get(`/huddle/articles/${articleId}/comments`);
            if (response.data.success) {
                const commentsData = response.data.data?.content || [];

                // Initialize vote counts from comments
                const voteCounts = {};
                commentsData.forEach(comment => {
                    voteCounts[comment.id] = {
                        upvotes: comment.upvotes || 0,
                        downvotes: comment.downvotes || 0
                    };
                });

                setComments(commentsData);
            }
        } catch (err) {
            console.error('Failed to fetch comments:', err);
        }
    };

    const handleAddComment = async () => {
        if (!newComment.trim()) return;
        try {
            const response = await axios.post(`/huddle/articles/${articleId}/comments`, {
                body: newComment
            });
            if (response.data.success) {
                setNewComment('');
                fetchComments();
            }
        } catch (err) {
            setError('Failed to add comment');
        }
    };

    const handleEditComment = (comment) => {
        setEditingCommentId(comment.id);
        setEditingCommentText(comment.body);
    };

    const handleSaveEdit = async (commentId) => {
        if (!editingCommentText.trim()) return;

        try {
            const response = await axios.put(`/huddle/articles/${articleId}/comments/${commentId}`, {
                body: editingCommentText
            });
            if (response.data.success) {
                setEditingCommentId(null);
                setEditingCommentText('');
                fetchComments();
            }
        } catch (err) {
            setError('Failed to update comment');
        }
    };

    const handleCancelEdit = () => {
        setEditingCommentId(null);
        setEditingCommentText('');
    };

    const handleDeleteComment = async (commentId) => {
        if (!window.confirm('Are you sure you want to delete this comment?')) return;

        try {
            await axios.delete(`/huddle/articles/${articleId}/comments/${commentId}`);
            fetchComments();
        } catch (err) {
            setError('Failed to delete comment');
        }
    };

    const handleVoteComment = async (commentId, voteType) => {
        try {
            const response = await axios.post(`/huddle/comments/${commentId}/vote`, null, {
                params: { voteType: voteType }
            });

            // Update local state for immediate feedback
            setUserVotes(prev => {
                const newVotes = { ...prev };
                const currentVote = newVotes[commentId];

                if (currentVote === voteType) {
                    // If clicking same vote, remove it
                    delete newVotes[commentId];
                } else {
                    // Set new vote
                    newVotes[commentId] = voteType;
                }
                return newVotes;
            });

            // Update comment vote counts locally for immediate feedback
            setComments(prevComments => {
                return prevComments.map(comment => {
                    if (comment.id === commentId) {
                        const currentVote = userVotes[commentId];
                        let upvotes = comment.upvotes || 0;
                        let downvotes = comment.downvotes || 0;

                        // Remove previous vote
                        if (currentVote === 'UPVOTE') upvotes--;
                        if (currentVote === 'DOWNVOTE') downvotes--;

                        // Add new vote or toggle off
                        if (currentVote !== voteType) {
                            if (voteType === 'UPVOTE') upvotes++;
                            if (voteType === 'DOWNVOTE') downvotes++;
                        }

                        return { ...comment, upvotes, downvotes };
                    }
                    return comment;
                });
            });

            // Optionally refresh from server
            // fetchComments();
        } catch (err) {
            console.error('Failed to vote on comment:', err);
        }
    };

    const handleShare = () => {
        if (navigator.share) {
            navigator.share({
                title: article.title,
                text: article.content.substring(0, 100) + '...',
                url: window.location.href,
            });
        } else {
            navigator.clipboard.writeText(window.location.href);
            alert('Link copied to clipboard!');
        }
    };

    const calculateReadTime = (content) => {
        const wordsPerMinute = 200;
        const words = content?.split(' ').length || 0;
        const minutes = Math.ceil(words / wordsPerMinute);
        return `${minutes} min read`;
    };

    if (loading) {
        return (
            <Container maxWidth="md" sx={{ mt: 4 }}>
                <Stack spacing={2}>
                    <Skeleton variant="text" sx={{ fontSize: '3rem' }} />
                    <Skeleton variant="rectangular" height={60} />
                    <Skeleton variant="rectangular" height={400} />
                </Stack>
            </Container>
        );
    }

    if (!article) {
        return (
            <Container maxWidth="md" sx={{ mt: 4 }}>
                <Paper sx={{ p: 4, textAlign: 'center' }}>
                    <Alert severity="error">Article not found</Alert>
                    <Button
                        startIcon={<ArrowBack />}
                        onClick={() => navigate('/articles')}
                        sx={{ mt: 2 }}
                    >
                        Back to Articles
                    </Button>
                </Paper>
            </Container>
        );
    }

    return (
        <>
            {/* Hero Section */}
            <Box
                sx={{
                    background: `linear-gradient(135deg, ${alpha(theme.palette.primary.main, 0.1)} 0%, ${alpha(theme.palette.secondary.main, 0.1)} 100%)`,
                    borderBottom: `1px solid ${theme.palette.divider}`,
                    mb: 4,
                    pt: 4,
                    pb: 6
                }}
            >
                <Container maxWidth="md">
                    <Button
                        startIcon={<ArrowBack />}
                        onClick={() => navigate(-1)}
                        sx={{ mb: 3 }}
                    >
                        Back
                    </Button>

                    <Typography
                        variant="h3"
                        component="h1"
                        gutterBottom
                        sx={{ fontWeight: 700 }}
                    >
                        {article.title}
                    </Typography>

                    <Box display="flex" alignItems="center" justifyContent="space-between" flexWrap="wrap" gap={2}>
                        <Box display="flex" alignItems="center" gap={2}>
                            <Avatar sx={{ width: 48, height: 48, bgcolor: theme.palette.primary.main }}>
                                {article.authorUsername?.[0]?.toUpperCase()}
                            </Avatar>
                            <Box>
                                <Typography variant="subtitle1" fontWeight={600}>
                                    {article.authorUsername}
                                </Typography>
                                <Box display="flex" alignItems="center" gap={2} color="text.secondary">
                                    <Typography variant="caption">
                                        {new Date(article.createdAt).toLocaleDateString('en-US', {
                                            year: 'numeric',
                                            month: 'long',
                                            day: 'numeric'
                                        })}
                                    </Typography>
                                    <Box display="flex" alignItems="center" gap={0.5}>
                                        <AccessTime fontSize="small" />
                                        <Typography variant="caption">
                                            {calculateReadTime(article.content)}
                                        </Typography>
                                    </Box>
                                </Box>
                            </Box>
                        </Box>

                        <Stack direction="row" spacing={1}>
                            {article.authorUsername === currentUsername && (
                                <Button
                                    variant="outlined"
                                    startIcon={<Edit />}
                                    onClick={() => navigate(`/articles/edit/${article.id}`)}
                                >
                                    Edit Article
                                </Button>
                            )}
                            <IconButton onClick={handleShare}>
                                <Share />
                            </IconButton>
                        </Stack>
                    </Box>

                    {article.tags && article.tags.length > 0 && (
                        <Box mt={3}>
                            {article.tags.map((tag, idx) => (
                                <Chip key={idx} label={tag} sx={{ mr: 1, mb: 1 }} variant="outlined" color="primary" />
                            ))}
                        </Box>
                    )}
                </Container>
            </Box>

            <Container maxWidth="md">
                <Paper elevation={0} sx={{ p: 4, mb: 4, borderRadius: 2 }}>
                    <Typography
                        variant="body1"
                        sx={{ fontSize: '1.125rem', lineHeight: 1.8, whiteSpace: 'pre-wrap' }}
                    >
                        {article.content}
                    </Typography>
                </Paper>

                {/* Rating Info */}
                {(article.averageRating || article.totalRatings > 0) && (
                    <Paper elevation={1} sx={{ p: 2, mb: 4 }}>
                        <Stack direction="row" spacing={3}>
                            {article.averageRating && (
                                <Box>
                                    <Typography variant="h6">{article.averageRating.toFixed(1)}</Typography>
                                    <Typography variant="caption" color="textSecondary">Average Rating</Typography>
                                </Box>
                            )}
                            {article.totalRatings > 0 && (
                                <Box>
                                    <Typography variant="h6">{article.totalRatings}</Typography>
                                    <Typography variant="caption" color="textSecondary">Total Ratings</Typography>
                                </Box>
                            )}
                        </Stack>
                    </Paper>
                )}

                {/* Comments Section */}
                <Card sx={{ mb: 4 }}>
                    <CardContent>
                        <Typography variant="h5" gutterBottom sx={{ fontWeight: 600 }}>
                            <CommentIcon sx={{ mr: 1, verticalAlign: 'bottom' }} />
                            Comments ({comments.length})
                        </Typography>

                        <Box sx={{ mb: 3 }}>
                            <TextField
                                fullWidth
                                multiline
                                rows={3}
                                placeholder="Share your thoughts..."
                                value={newComment}
                                onChange={(e) => setNewComment(e.target.value)}
                                variant="outlined"
                                sx={{ mb: 2 }}
                            />
                            <Button
                                variant="contained"
                                onClick={handleAddComment}
                                disabled={!newComment.trim()}
                            >
                                Post Comment
                            </Button>
                        </Box>

                        <Divider sx={{ my: 3 }} />

                        <List>
                            {comments.map((comment) => {
                                const isUpvoted = userVotes[comment.id] === 'UPVOTE';
                                const isDownvoted = userVotes[comment.id] === 'DOWNVOTE';

                                return (
                                    <React.Fragment key={comment.id}>
                                        <ListItem alignItems="flex-start" sx={{ px: 0, py: 2 }}>
                                            <ListItemAvatar>
                                                <Avatar sx={{ bgcolor: theme.palette.primary.main }}>
                                                    {comment.authorUsername?.[0]?.toUpperCase()}
                                                </Avatar>
                                            </ListItemAvatar>
                                            <ListItemText
                                                sx={{ flex: 1 }}
                                                primary={
                                                    <Box display="flex" alignItems="center" gap={1} mb={1}>
                                                        <Typography variant="subtitle2" fontWeight={600}>
                                                            {comment.authorUsername}
                                                        </Typography>
                                                        <Typography variant="caption" color="textSecondary">
                                                            {new Date(comment.createdAt).toLocaleDateString()}
                                                        </Typography>
                                                        {comment.updatedAt && comment.updatedAt !== comment.createdAt && (
                                                            <Typography variant="caption" color="textSecondary" fontStyle="italic">
                                                                (edited)
                                                            </Typography>
                                                        )}
                                                    </Box>
                                                }
                                                secondary={
                                                    <>
                                                        {editingCommentId === comment.id ? (
                                                            <Box sx={{ mt: 1 }}>
                                                                <TextField
                                                                    fullWidth
                                                                    multiline
                                                                    rows={3}
                                                                    value={editingCommentText}
                                                                    onChange={(e) => setEditingCommentText(e.target.value)}
                                                                    variant="outlined"
                                                                    size="small"
                                                                    sx={{ mb: 1 }}
                                                                    autoFocus
                                                                />
                                                                <Stack direction="row" spacing={1}>
                                                                    <Button
                                                                        size="small"
                                                                        variant="contained"
                                                                        startIcon={<Check />}
                                                                        onClick={() => handleSaveEdit(comment.id)}
                                                                        disabled={!editingCommentText.trim()}
                                                                    >
                                                                        Save
                                                                    </Button>
                                                                    <Button
                                                                        size="small"
                                                                        variant="outlined"
                                                                        startIcon={<Close />}
                                                                        onClick={handleCancelEdit}
                                                                    >
                                                                        Cancel
                                                                    </Button>
                                                                </Stack>
                                                            </Box>
                                                        ) : (
                                                            <>
                                                                <Typography variant="body2" sx={{ mt: 1, mb: 2, whiteSpace: 'pre-wrap' }}>
                                                                    {comment.body}
                                                                </Typography>
                                                                <Stack direction="row" spacing={2} alignItems="center">
                                                                    <Box display="flex" alignItems="center">
                                                                        <IconButton
                                                                            size="small"
                                                                            onClick={() => handleVoteComment(comment.id, 'UPVOTE')}
                                                                            sx={{
                                                                                color: isUpvoted ? theme.palette.primary.main : 'inherit'
                                                                            }}
                                                                        >
                                                                            <ThumbUp fontSize="small" />
                                                                        </IconButton>
                                                                        <Typography variant="caption" sx={{ mx: 0.5 }}>
                                                                            {comment.upvotes || 0}
                                                                        </Typography>
                                                                    </Box>
                                                                    <Box display="flex" alignItems="center">
                                                                        <IconButton
                                                                            size="small"
                                                                            onClick={() => handleVoteComment(comment.id, 'DOWNVOTE')}
                                                                            sx={{
                                                                                color: isDownvoted ? theme.palette.error.main : 'inherit'
                                                                            }}
                                                                        >
                                                                            <ThumbDown fontSize="small" />
                                                                        </IconButton>
                                                                        <Typography variant="caption" sx={{ mx: 0.5 }}>
                                                                            {comment.downvotes || 0}
                                                                        </Typography>
                                                                    </Box>
                                                                </Stack>
                                                            </>
                                                        )}
                                                    </>
                                                }
                                            />
                                            {comment.authorUsername === currentUsername && editingCommentId !== comment.id && (
                                                <Stack direction="row" spacing={0.5}>
                                                    <IconButton
                                                        size="small"
                                                        onClick={() => handleEditComment(comment)}
                                                        color="primary"
                                                    >
                                                        <Edit fontSize="small" />
                                                    </IconButton>
                                                    <IconButton
                                                        size="small"
                                                        onClick={() => handleDeleteComment(comment.id)}
                                                        color="error"
                                                    >
                                                        <Delete fontSize="small" />
                                                    </IconButton>
                                                </Stack>
                                            )}
                                        </ListItem>
                                        <Divider variant="inset" component="li" />
                                    </React.Fragment>
                                );
                            })}
                        </List>

                        {comments.length === 0 && (
                            <Box sx={{ py: 4, textAlign: 'center' }}>
                                <Typography color="textSecondary">
                                    No comments yet. Be the first to share your thoughts!
                                </Typography>
                            </Box>
                        )}
                    </CardContent>
                </Card>

                {error && (
                    <Alert severity="error" onClose={() => setError('')} sx={{ mb: 2 }}>
                        {error}
                    </Alert>
                )}
            </Container>
        </>
    );
}

export default ArticleDetail;
