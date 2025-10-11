import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Container,
    Typography,
    Button,
    Grid,
    Card,
    CardContent,
    CardActions,
    Box,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Alert,
    Chip,
    IconButton,
    Paper,
    InputAdornment,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    Skeleton,
    Avatar,
    ToggleButton,
    ToggleButtonGroup,
    useTheme,
    alpha,
    Stack,
    Fade,
    Rating,
    Menu
} from '@mui/material';
import {
    Add,
    Edit,
    Delete,
    Search,
    Article as ArticleIcon,
    ViewModule,
    ViewList,
    Share,
    PersonOutline,
    Public,
    Star,
    StarBorder,
    TrendingUp,
    Flag,
    Report,
    MoreVert
} from '@mui/icons-material';
import axios from 'axios';

function Articles() {
    const navigate = useNavigate();
    const theme = useTheme();
    const [articles, setArticles] = useState([]);
    const [loading, setLoading] = useState(true);
    const [openDialog, setOpenDialog] = useState(false);
    const [editMode, setEditMode] = useState(false);
    const [currentArticle, setCurrentArticle] = useState({
        title: '',
        content: '',
        tags: ''
    });
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [activeTab, setActiveTab] = useState('all');
    const [searchTerm, setSearchTerm] = useState('');
    const [sortBy, setSortBy] = useState('newest');
    const [viewMode, setViewMode] = useState('grid');
    const [userRatings, setUserRatings] = useState({});
    const currentUsername = localStorage.getItem('username');

    // Report functionality states
    const [reportDialog, setReportDialog] = useState(false);
    const [reportingArticle, setReportingArticle] = useState(null);
    const [reportReason, setReportReason] = useState('');
    const [reportDescription, setReportDescription] = useState('');
    const [anchorEl, setAnchorEl] = useState(null);
    const [menuArticle, setMenuArticle] = useState(null);

    useEffect(() => {
        fetchArticles();
    }, [activeTab, sortBy]);

    const fetchArticles = async () => {
        setLoading(true);
        try {
            let response;
            if (activeTab === 'my') {
                response = await axios.get('/huddle/articles/my-articles', {
                    params: { size: 50, sort: getSortParam() }
                });
            } else {
                response = await axios.get('/huddle/articles/allArticles', {
                    params: { size: 50, sort: getSortParam() }
                });
            }

            if (response.data.success) {
                const articlesData = response.data.data.content || [];
                setArticles(articlesData);
            }
        } catch (err) {
            setError('Failed to fetch articles');
            console.error('Failed to fetch articles:', err);
        } finally {
            setLoading(false);
        }
    };

    const getSortParam = () => {
        switch (sortBy) {
            case 'newest': return 'createdAt,desc';
            case 'oldest': return 'createdAt,asc';
            case 'popular': return 'averageRating,desc';
            default: return 'createdAt,desc';
        }
    };

    const handleSubmit = async () => {
        try {
            const url = editMode
                ? `/huddle/articles/update/${currentArticle.id}`
                : '/huddle/articles/create';

            const method = editMode ? 'put' : 'post';

            await axios[method](url, {
                title: currentArticle.title,
                content: currentArticle.content,
                tags: currentArticle.tags.split(',').map(tag => tag.trim()).filter(tag => tag)
            });

            setSuccess(editMode ? 'Article updated successfully!' : 'Article created successfully!');
            setOpenDialog(false);
            setCurrentArticle({ title: '', content: '', tags: '' });
            fetchArticles();
        } catch (err) {
            setError('Failed to save article');
        }
    };

    const handleDelete = async (e, id) => {
        e.stopPropagation();
        if (window.confirm('Are you sure you want to delete this article?')) {
            try {
                await axios.delete(`/huddle/articles/delete/${id}`);
                setSuccess('Article deleted successfully!');
                fetchArticles();
            } catch (err) {
                setError('Failed to delete article');
            }
        }
    };

    const handleEdit = (e, article) => {
        e.stopPropagation();
        setCurrentArticle({
            id: article.id,
            title: article.title,
            content: article.content,
            tags: article.tags ? article.tags.join(', ') : ''
        });
        setEditMode(true);
        setOpenDialog(true);
    };

    const handleAdd = () => {
        setCurrentArticle({ title: '', content: '', tags: '' });
        setEditMode(false);
        setOpenDialog(true);
    };

    const handleShare = (article) => {
        const url = `${window.location.origin}/articles/${article.id}`;
        if (navigator.share) {
            navigator.share({
                title: article.title,
                text: article.content.substring(0, 100) + '...',
                url: url,
            }).catch(err => console.log('Error sharing:', err));
        } else {
            navigator.clipboard.writeText(url);
            setSuccess('Link copied to clipboard!');
        }
    };

    const handleRateArticle = async (articleId, newValue) => {
        if (!newValue) return;

        try {
            const response = await axios.post(`/huddle/articles/${articleId}/rate`, null, {
                params: { rating: newValue }
            });

            if (response.data.success) {
                setUserRatings(prev => ({ ...prev, [articleId]: newValue }));

                setArticles(prevArticles =>
                    prevArticles.map(article => {
                        if (article.id === articleId) {
                            return {
                                ...article,
                                averageRating: response.data.data.rating || article.averageRating,
                                totalRatings: article.totalRatings + (userRatings[articleId] ? 0 : 1)
                            };
                        }
                        return article;
                    })
                );

                setSuccess('Article rated successfully!');
            }
        } catch (err) {
            console.error('Failed to rate article:', err);
            if (err.response?.status === 401) {
                setError('Please log in to rate articles');
            } else {
                setError('Failed to rate article. Please try again.');
            }
        }
    };

    // Report functionality handlers
    const handleReportArticle = async () => {
        if (!reportReason || !reportDescription.trim()) {
            setError('Please provide a reason and description for the report');
            return;
        }

        try {
            const response = await axios.post('/huddle/reports/create', {
                articleId: reportingArticle.id,
                reason: reportReason,
                description: reportDescription
            });

            if (response.data.success) {
                setSuccess('Article reported successfully. Our team will review it.');
                setReportDialog(false);
                setReportReason('');
                setReportDescription('');
                setReportingArticle(null);
            }
        } catch (err) {
            console.error('Failed to report article:', err);
            if (err.response?.status === 401) {
                setError('Please log in to report articles');
            } else if (err.response?.status === 400) {
                setError(err.response.data.message || 'Invalid report data');
            } else {
                setError('Failed to report article. Please try again.');
            }
        }
    };

    const handleMenuOpen = (event, article) => {
        event.stopPropagation();
        setAnchorEl(event.currentTarget);
        setMenuArticle(article);
    };

    const handleMenuClose = () => {
        setAnchorEl(null);
        setMenuArticle(null);
    };

    const handleOpenReport = (article) => {
        setReportingArticle(article);
        setReportDialog(true);
        handleMenuClose();
    };

    const filteredArticles = articles.filter(article =>
        article.title?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        article.content?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const getCardGradient = (index) => {
        const gradients = [
            `linear-gradient(135deg, ${alpha(theme.palette.primary.main, 0.8)} 0%, ${alpha(theme.palette.primary.dark, 0.9)} 100%)`,
            `linear-gradient(135deg, ${alpha(theme.palette.secondary.main, 0.8)} 0%, ${alpha(theme.palette.secondary.dark, 0.9)} 100%)`,
            `linear-gradient(135deg, ${alpha('#9333ea', 0.8)} 0%, ${alpha('#7c3aed', 0.9)} 100%)`,
            `linear-gradient(135deg, ${alpha('#ec4899', 0.8)} 0%, ${alpha('#db2777', 0.9)} 100%)`,
            `linear-gradient(135deg, ${alpha('#3b82f6', 0.8)} 0%, ${alpha('#2563eb', 0.9)} 100%)`,
            `linear-gradient(135deg, ${alpha('#10b981', 0.8)} 0%, ${alpha('#059669', 0.9)} 100%)`,
        ];
        return gradients[index % gradients.length];
    };

    const ArticleCard = ({ article, index }) => {
        const isListView = viewMode === 'list';
        const isOwnArticle = article.authorUsername === currentUsername;

        if (isListView) {
            return (
                <Fade in timeout={300 * (index % 6 + 1)}>
                    <Card
                        sx={{
                            display: 'flex',
                            transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                            cursor: 'pointer',
                            '&:hover': {
                                transform: 'translateY(-4px)',
                                boxShadow: theme.shadows[8],
                            }
                        }}
                        onClick={() => navigate(`/articles/${article.id}`)}
                    >
                        <Box sx={{ display: 'flex', width: '100%', p: 2 }}>
                            <Box sx={{ flex: 1 }}>
                                <Typography variant="h6" gutterBottom fontWeight={600}>
                                    {article.title}
                                </Typography>
                                <Box display="flex" alignItems="center" gap={1} mb={1}>
                                    <Avatar sx={{ width: 24, height: 24, fontSize: '0.75rem' }}>
                                        {article.authorUsername?.[0]?.toUpperCase()}
                                    </Avatar>
                                    <Typography variant="caption" color="textSecondary">
                                        {article.authorUsername} • {new Date(article.createdAt || article.createdDate).toLocaleDateString()}
                                    </Typography>
                                </Box>
                                <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
                                    {article.content?.substring(0, 150)}...
                                </Typography>
                                <Box display="flex" alignItems="center" gap={2}>
                                    <Rating
                                        value={userRatings[article.id] || article.averageRating || 0}
                                        onChange={(e, newValue) => {
                                            e.stopPropagation();
                                            handleRateArticle(article.id, newValue);
                                        }}
                                        onClick={(e) => e.stopPropagation()}
                                        size="small"
                                        precision={1}
                                    />
                                    {article.averageRating > 0 && (
                                        <Typography variant="caption" color="textSecondary">
                                            {article.averageRating.toFixed(1)} ({article.totalRatings || 0} ratings)
                                        </Typography>
                                    )}
                                </Box>
                            </Box>
                            <Box sx={{ display: 'flex', gap: 1 }}>
                                {isOwnArticle ? (
                                    <>
                                        <IconButton onClick={(e) => handleEdit(e, article)}>
                                            <Edit />
                                        </IconButton>
                                        <IconButton onClick={(e) => handleDelete(e, article.id)} color="error">
                                            <Delete />
                                        </IconButton>
                                    </>
                                ) : (
                                    <IconButton onClick={(e) => handleMenuOpen(e, article)}>
                                        <MoreVert />
                                    </IconButton>
                                )}
                            </Box>
                        </Box>
                    </Card>
                </Fade>
            );
        }

        // Grid View
        return (
            <Fade in timeout={300 * (index % 6 + 1)}>
                <Card
                    sx={{
                        height: '100%',
                        display: 'flex',
                        flexDirection: 'column',
                        transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                        cursor: 'pointer',
                        position: 'relative',
                        overflow: 'hidden',
                        '&:hover': {
                            transform: 'translateY(-8px) scale(1.02)',
                            boxShadow: theme.shadows[20],
                            '& .card-media': {
                                transform: 'scale(1.1)',
                            }
                        }
                    }}
                    onClick={() => navigate(`/articles/${article.id}`)}
                >
                    <Box
                        className="card-media"
                        sx={{
                            height: 160,
                            background: getCardGradient(index),
                            position: 'relative',
                            transition: 'transform 0.3s ease',
                            display: 'flex',
                            flexDirection: 'column',
                            justifyContent: 'flex-end',
                            padding: 2
                        }}
                    >
                        <Typography
                            variant="h6"
                            sx={{
                                color: 'white',
                                fontWeight: 700,
                                textShadow: '0 2px 4px rgba(0,0,0,0.2)',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                                display: '-webkit-box',
                                WebkitLineClamp: 2,
                                WebkitBoxOrient: 'vertical',
                            }}
                        >
                            {article.title}
                        </Typography>

                        <Box sx={{ position: 'absolute', top: 8, right: 8 }}>
                            {isOwnArticle ? (
                                <>
                                    <IconButton
                                        size="small"
                                        onClick={(e) => handleEdit(e, article)}
                                        sx={{
                                            color: 'white',
                                            bgcolor: alpha(theme.palette.background.paper, 0.2),
                                            backdropFilter: 'blur(4px)',
                                            mr: 0.5,
                                            '&:hover': {
                                                bgcolor: alpha(theme.palette.background.paper, 0.4)
                                            }
                                        }}
                                    >
                                        <Edit fontSize="small" />
                                    </IconButton>
                                    <IconButton
                                        size="small"
                                        onClick={(e) => handleDelete(e, article.id)}
                                        sx={{
                                            color: 'white',
                                            bgcolor: alpha(theme.palette.error.main, 0.7),
                                            backdropFilter: 'blur(4px)',
                                            '&:hover': {
                                                bgcolor: alpha(theme.palette.error.main, 0.9)
                                            }
                                        }}
                                    >
                                        <Delete fontSize="small" />
                                    </IconButton>
                                </>
                            ) : (
                                <IconButton
                                    size="small"
                                    onClick={(e) => handleMenuOpen(e, article)}
                                    sx={{
                                        color: 'white',
                                        bgcolor: alpha(theme.palette.background.paper, 0.2),
                                        backdropFilter: 'blur(4px)',
                                        '&:hover': {
                                            bgcolor: alpha(theme.palette.background.paper, 0.4)
                                        }
                                    }}
                                >
                                    <MoreVert fontSize="small" />
                                </IconButton>
                            )}
                        </Box>
                    </Box>

                    <CardContent sx={{ flexGrow: 1, pb: 1 }}>
                        <Box display="flex" alignItems="center" gap={1} mb={2}>
                            <Avatar
                                sx={{
                                    width: 32,
                                    height: 32,
                                    bgcolor: theme.palette.primary.main,
                                    fontSize: '0.875rem',
                                    cursor: 'pointer'
                                }}
                                onClick={(e) => {
                                    e.stopPropagation();
                                    navigate(`/profile/${article.authorUsername}`);
                                }}
                            >
                                {article.authorUsername?.[0]?.toUpperCase()}
                            </Avatar>
                            <Box flex={1}>
                                <Typography
                                    variant="body2"
                                    fontWeight={600}
                                    sx={{
                                        cursor: 'pointer',
                                        '&:hover': { textDecoration: 'underline' }
                                    }}
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        navigate(`/profile/${article.authorUsername}`);
                                    }}
                                >
                                    {article.authorUsername}
                                </Typography>
                                <Typography variant="caption" color="textSecondary">
                                    {new Date(article.createdAt || article.createdDate).toLocaleDateString('en-US', {
                                        month: 'short',
                                        day: 'numeric',
                                        year: 'numeric'
                                    })}
                                </Typography>
                            </Box>
                        </Box>

                        <Typography
                            variant="body2"
                            color="textSecondary"
                            sx={{
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                                display: '-webkit-box',
                                WebkitLineClamp: 3,
                                WebkitBoxOrient: 'vertical',
                                lineHeight: 1.6,
                                minHeight: '3.6em'
                            }}
                        >
                            {article.content}
                        </Typography>

                        {article.tags && article.tags.length > 0 && (
                            <Box sx={{ mt: 2 }}>
                                {article.tags.slice(0, 2).map((tag, idx) => (
                                    <Chip
                                        key={idx}
                                        label={tag}
                                        size="small"
                                        sx={{ mr: 0.5, mb: 0.5 }}
                                        variant="outlined"
                                    />
                                ))}
                                {article.tags.length > 2 && (
                                    <Chip
                                        label={`+${article.tags.length - 2}`}
                                        size="small"
                                        sx={{ mb: 0.5 }}
                                        variant="outlined"
                                        color="primary"
                                    />
                                )}
                            </Box>
                        )}
                    </CardContent>

                    <CardActions sx={{ px: 2, py: 1.5, borderTop: `1px solid ${theme.palette.divider}` }}>
                        <Stack direction="row" spacing={1} width="100%" alignItems="center">
                            <Box display="flex" alignItems="center" gap={1}>
                                <Rating
                                    value={userRatings[article.id] || article.averageRating || 0}
                                    onChange={(e, newValue) => {
                                        e.stopPropagation();
                                        handleRateArticle(article.id, newValue);
                                    }}
                                    onClick={(e) => e.stopPropagation()}
                                    size="small"
                                    precision={1}
                                    sx={{
                                        '& .MuiRating-iconFilled': {
                                            color: theme.palette.warning.main,
                                        },
                                        '& .MuiRating-iconHover': {
                                            color: theme.palette.warning.dark,
                                        }
                                    }}
                                />
                                {article.averageRating > 0 && (
                                    <Typography variant="caption" color="textSecondary">
                                        {article.averageRating.toFixed(1)}
                                    </Typography>
                                )}
                                {article.totalRatings > 0 && (
                                    <Typography variant="caption" color="textSecondary">
                                        ({article.totalRatings})
                                    </Typography>
                                )}
                            </Box>

                            <Box display="flex" alignItems="center" gap={0.5} ml="auto">
                                <IconButton
                                    size="small"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        handleShare(article);
                                    }}
                                >
                                    <Share fontSize="small" />
                                </IconButton>
                                {!isOwnArticle && (
                                    <IconButton
                                        size="small"
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            handleOpenReport(article);
                                        }}
                                        sx={{ color: theme.palette.text.secondary }}
                                    >
                                        <Flag fontSize="small" />
                                    </IconButton>
                                )}
                            </Box>
                        </Stack>
                    </CardActions>
                </Card>
            </Fade>
        );
    };

    return (
        <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
            {/* Hero Section */}
            <Box
                sx={{
                    background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
                    color: 'white',
                    pt: 8,
                    pb: 10,
                    mb: -6
                }}
            >
                <Container maxWidth="lg">
                    <Typography variant="h3" fontWeight={800} gutterBottom>
                        Articles Hub
                    </Typography>
                    <Typography variant="h6" sx={{ mb: 4, opacity: 0.9 }}>
                        Discover, create, and share knowledge with the community
                    </Typography>

                    <Paper
                        elevation={3}
                        sx={{
                            p: 1,
                            borderRadius: 3,
                            maxWidth: 600,
                            bgcolor: 'background.paper'
                        }}
                    >
                        <TextField
                            fullWidth
                            placeholder="Search articles..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            variant="standard"
                            InputProps={{
                                disableUnderline: true,
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <Search color="action" />
                                    </InputAdornment>
                                ),
                                sx: { fontSize: '1.1rem', px: 1 }
                            }}
                        />
                    </Paper>
                </Container>
            </Box>

            <Container maxWidth="lg" sx={{ mt: 8 }}>
                {/* Controls */}
                <Paper elevation={1} sx={{ p: 2, mb: 3, borderRadius: 2 }}>
                    <Grid container spacing={2} alignItems="center">
                        <Grid item xs={12} md={3}>
                            <ToggleButtonGroup
                                value={activeTab}
                                exclusive
                                onChange={(e, value) => value && setActiveTab(value)}
                                fullWidth
                                size="small"
                            >
                                <ToggleButton value="all">
                                    <Public sx={{ mr: 1 }} /> All
                                </ToggleButton>
                                <ToggleButton value="my">
                                    <PersonOutline sx={{ mr: 1 }} /> My Articles
                                </ToggleButton>
                            </ToggleButtonGroup>
                        </Grid>

                        <Grid item xs={12} md={3}>
                            <FormControl fullWidth size="small">
                                <InputLabel>Sort By</InputLabel>
                                <Select
                                    value={sortBy}
                                    onChange={(e) => setSortBy(e.target.value)}
                                    label="Sort By"
                                >
                                    <MenuItem value="newest">Newest First</MenuItem>
                                    <MenuItem value="oldest">Oldest First</MenuItem>
                                    <MenuItem value="popular">Most Popular</MenuItem>
                                </Select>
                            </FormControl>
                        </Grid>

                        <Grid item xs={12} md={3}>
                            <ToggleButtonGroup
                                value={viewMode}
                                exclusive
                                onChange={(e, value) => value && setViewMode(value)}
                                fullWidth
                                size="small"
                            >
                                <ToggleButton value="grid">
                                    <ViewModule />
                                </ToggleButton>
                                <ToggleButton value="list">
                                    <ViewList />
                                </ToggleButton>
                            </ToggleButtonGroup>
                        </Grid>

                        <Grid item xs={12} md={3}>
                            <Button
                                fullWidth
                                variant="contained"
                                startIcon={<Add />}
                                onClick={handleAdd}
                                sx={{
                                    borderRadius: 2,
                                    py: 1,
                                    background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.primary.dark} 100%)`,
                                }}
                            >
                                New Article
                            </Button>
                        </Grid>
                    </Grid>
                </Paper>

                {/* Alerts */}
                {error && <Alert severity="error" onClose={() => setError('')} sx={{ mb: 2 }}>{error}</Alert>}
                {success && <Alert severity="success" onClose={() => setSuccess('')} sx={{ mb: 2 }}>{success}</Alert>}

                {/* Articles Grid */}
                {loading ? (
                    <Grid container spacing={3}>
                        {[...Array(6)].map((_, n) => (
                            <Grid item xs={12} sm={6} md={4} key={n}>
                                <Skeleton variant="rectangular" height={320} sx={{ borderRadius: 2 }} />
                            </Grid>
                        ))}
                    </Grid>
                ) : filteredArticles.length === 0 ? (
                    <Paper
                        elevation={0}
                        sx={{
                            p: 6,
                            textAlign: 'center',
                            borderRadius: 3,
                            border: `2px dashed ${theme.palette.divider}`
                        }}
                    >
                        <ArticleIcon sx={{ fontSize: 80, color: 'text.secondary', mb: 2 }} />
                        <Typography variant="h5" color="textSecondary" gutterBottom>
                            {searchTerm ? 'No articles found' : 'No articles yet'}
                        </Typography>
                        <Typography variant="body1" color="textSecondary" paragraph>
                            {activeTab === 'my'
                                ? "Start sharing your knowledge with the community"
                                : "Be the first to contribute"}
                        </Typography>
                        <Button
                            variant="contained"
                            startIcon={<Add />}
                            onClick={handleAdd}
                            size="large"
                            sx={{ borderRadius: 2, px: 4 }}
                        >
                            Create Article
                        </Button>
                    </Paper>
                ) : (
                    <Grid container spacing={3}>
                        {filteredArticles.map((article, index) => (
                            <Grid
                                item
                                xs={12}
                                sm={6}
                                md={viewMode === 'list' ? 12 : 4}
                                key={article.id}
                            >
                                <ArticleCard article={article} index={index} />
                            </Grid>
                        ))}
                    </Grid>
                )}

                {/* Article Form Dialog */}
                <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
                    <DialogTitle sx={{ pb: 1 }}>
                        <Typography variant="h5" fontWeight={600}>
                            {editMode ? 'Edit Article' : 'Create New Article'}
                        </Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Box sx={{ mt: 2 }}>
                            <TextField
                                fullWidth
                                label="Title"
                                variant="outlined"
                                value={currentArticle.title}
                                onChange={(e) => setCurrentArticle({...currentArticle, title: e.target.value})}
                                sx={{ mb: 3 }}
                                placeholder="Enter an engaging title..."
                            />
                            <TextField
                                fullWidth
                                multiline
                                rows={15}
                                label="Content"
                                variant="outlined"
                                value={currentArticle.content}
                                onChange={(e) => setCurrentArticle({...currentArticle, content: e.target.value})}
                                sx={{ mb: 3 }}
                                placeholder="Share your thoughts, ideas, or knowledge..."
                            />
                            <TextField
                                fullWidth
                                label="Tags"
                                variant="outlined"
                                value={currentArticle.tags}
                                onChange={(e) => setCurrentArticle({...currentArticle, tags: e.target.value})}
                                placeholder="technology, programming, react (comma separated)"
                                helperText="Add tags to help others discover your article"
                            />
                        </Box>
                    </DialogContent>
                    <DialogActions sx={{ p: 2.5 }}>
                        <Button onClick={() => setOpenDialog(false)} size="large">
                            Cancel
                        </Button>
                        <Button
                            onClick={handleSubmit}
                            variant="contained"
                            size="large"
                            disabled={!currentArticle.title.trim() || !currentArticle.content.trim()}
                            sx={{ px: 4 }}
                        >
                            {editMode ? 'Update' : 'Publish'}
                        </Button>
                    </DialogActions>
                </Dialog>

                {/* Menu for article actions */}
                <Menu
                    anchorEl={anchorEl}
                    open={Boolean(anchorEl)}
                    onClose={handleMenuClose}
                    onClick={(e) => e.stopPropagation()}
                >
                    <MenuItem onClick={() => handleOpenReport(menuArticle)}>
                        <Flag fontSize="small" sx={{ mr: 1 }} />
                        Report Article
                    </MenuItem>
                </Menu>

                {/* Report Dialog */}
                <Dialog
                    open={reportDialog}
                    onClose={() => {
                        setReportDialog(false);
                        setReportReason('');
                        setReportDescription('');
                        setReportingArticle(null);
                    }}
                    maxWidth="sm"
                    fullWidth
                >
                    <DialogTitle sx={{ pb: 1 }}>
                        <Typography variant="h5" fontWeight={600}>
                            Report Article
                        </Typography>
                    </DialogTitle>
                    <DialogContent>
                        <Box sx={{ mt: 2 }}>
                            <Typography variant="body2" color="textSecondary" sx={{ mb: 2 }}>
                                Please provide details about why you're reporting this article. This helps us maintain a safe community.
                            </Typography>

                            <FormControl fullWidth sx={{ mb: 3 }}>
                                <InputLabel>Reason for Report</InputLabel>
                                <Select
                                    value={reportReason}
                                    onChange={(e) => setReportReason(e.target.value)}
                                    label="Reason for Report"
                                >
                                    <MenuItem value="INAPPROPRIATE_CONTENT">Inappropriate Content</MenuItem>
                                    <MenuItem value="SPAM">Spam or Misleading</MenuItem>
                                    <MenuItem value="HARASSMENT">Harassment or Hate Speech</MenuItem>
                                    <MenuItem value="COPYRIGHT">Copyright Violation</MenuItem>
                                    <MenuItem value="FALSE_INFORMATION">False Information</MenuItem>
                                    <MenuItem value="OTHER">Other</MenuItem>
                                </Select>
                            </FormControl>

                            <TextField
                                fullWidth
                                multiline
                                rows={4}
                                label="Description"
                                variant="outlined"
                                value={reportDescription}
                                onChange={(e) => setReportDescription(e.target.value)}
                                placeholder="Please provide additional details about your report..."
                                helperText="Be as specific as possible to help us review this report"
                            />
                        </Box>
                    </DialogContent>
                    <DialogActions sx={{ p: 2.5 }}>
                        <Button
                            onClick={() => {
                                setReportDialog(false);
                                setReportReason('');
                                setReportDescription('');
                                setReportingArticle(null);
                            }}
                            size="large"
                        >
                            Cancel
                        </Button>
                        <Button
                            onClick={handleReportArticle}
                            variant="contained"
                            size="large"
                            disabled={!reportReason || !reportDescription.trim()}
                            color="error"
                            startIcon={<Report />}
                        >
                            Submit Report
                        </Button>
                    </DialogActions>
                </Dialog>
            </Container>
        </Box>
    );
}

export default Articles;