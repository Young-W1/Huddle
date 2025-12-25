import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import {
    Container,
    Typography,
    Tabs,
    Tab,
    Box,
    Card,
    CardContent,
    CardActions,
    Button,
    List,
    ListItem,
    ListItemAvatar,
    ListItemText,
    Avatar,
    Chip,
    CircularProgress,
    Pagination,
    Paper,
    TextField,
    InputAdornment
} from '@mui/material';
import {
    Search as SearchIcon,
    Person as PersonIcon,
    Article as ArticleIcon,
    Notifications as NotificationIcon
} from '@mui/icons-material';
import axios from 'axios';

function Search() {
    const [searchParams, setSearchParams] = useSearchParams();
    const [query, setQuery] = useState(searchParams.get('q') || '');
    const [tabValue, setTabValue] = useState(0);
    const [results, setResults] = useState(null);
    const [loading, setLoading] = useState(false);
    const [page, setPage] = useState(0);
    const navigate = useNavigate();

    useEffect(() => {
        const searchQuery = searchParams.get('q');
        if (searchQuery) {
            setQuery(searchQuery);
            performSearch(searchQuery, page);
        }
    }, [searchParams, page]);

    const performSearch = async (searchQuery, currentPage = 0) => {
        if (!searchQuery.trim()) return;

        setLoading(true);
        try {
            const response = await axios.get('/huddle/search/global', {
                params: {
                    query: searchQuery,
                    page: currentPage,
                    size: 10
                }
            });
            setResults(response.data);
            setLoading(false);
        } catch (err) {
            console.error('Search failed:', err);
            setLoading(false);
        }
    };

    const handleSearch = (e) => {
        e.preventDefault();
        setSearchParams({ q: query });
        setPage(0);
    };

    const handlePageChange = (event, value) => {
        setPage(value - 1);
    };

    const renderUsers = () => {
        if (!results?.users?.content?.length) {
            return <Typography color="textSecondary">No users found</Typography>;
        }

        return (
            <>
                <List>
                    {results.users.content.map((user) => (
                        <ListItem
                            key={user.id}
                            button
                            onClick={() => navigate(`/profile/${user.id}`)}
                            sx={{ mb: 1, backgroundColor: '#f5f5f5', borderRadius: 1 }}
                        >
                            <ListItemAvatar>
                                <Avatar>
                                    <PersonIcon />
                                </Avatar>
                            </ListItemAvatar>
                            <ListItemText
                                primary={user.displayName || user.username}
                                secondary={
                                    <>
                                        @{user.username}
                                        {user.bio && <Typography variant="body2">{user.bio}</Typography>}
                                    </>
                                }
                            />
                            <Button variant="outlined" size="small">
                                View Profile
                            </Button>
                        </ListItem>
                    ))}
                </List>
                {results.users.totalPages > 1 && (
                    <Pagination
                        count={results.users.totalPages}
                        page={page + 1}
                        onChange={handlePageChange}
                        sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}
                    />
                )}
            </>
        );
    };

    const renderArticles = () => {
        if (!results?.articles?.content?.length) {
            return <Typography color="textSecondary">No articles found</Typography>;
        }

        return (
            <>
                <List>
                    {results.articles.content.map((article) => (
                        <Card key={article.id} sx={{ mb: 2 }}>
                            <CardContent>
                                <Typography variant="h6">{article.title}</Typography>
                                <Typography variant="body2" color="textSecondary">
                                    by {article.authorUsername} • {new Date(article.createdDate).toLocaleDateString()}
                                </Typography>
                                <Typography variant="body2" sx={{ mt: 1, mb: 1 }}>
                                    {article.content?.substring(0, 200)}...
                                </Typography>
                                {article.tags?.length > 0 && (
                                    <Box sx={{ mt: 1 }}>
                                        {article.tags.map((tag, idx) => (
                                            <Chip
                                                key={idx}
                                                label={tag}
                                                size="small"
                                                sx={{ mr: 0.5 }}
                                            />
                                        ))}
                                    </Box>
                                )}
                            </CardContent>
                            <CardActions>
                                <Button
                                    size="small"
                                    onClick={() => navigate(`/articles/${article.id}`)}
                                >
                                    Read More
                                </Button>
                            </CardActions>
                        </Card>
                    ))}
                </List>
                {results.articles.totalPages > 1 && (
                    <Pagination
                        count={results.articles.totalPages}
                        page={page + 1}
                        onChange={handlePageChange}
                        sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}
                    />
                )}
            </>
        );
    };

    const renderNotifications = () => {
        if (!results?.notifications?.content?.length) {
            return <Typography color="textSecondary">No notifications found</Typography>;
        }

        return (
            <>
                <List>
                    {results.notifications.content.map((notification) => (
                        <Paper key={notification.id} sx={{ p: 2, mb: 1 }}>
                            <Typography variant="body1">{notification.message}</Typography>
                            <Typography variant="caption" color="textSecondary">
                                {notification.notificationType} • {new Date(notification.createdAt).toLocaleString()}
                            </Typography>
                        </Paper>
                    ))}
                </List>
                {results.notifications.totalPages > 1 && (
                    <Pagination
                        count={results.notifications.totalPages}
                        page={page + 1}
                        onChange={handlePageChange}
                        sx={{ mt: 2, display: 'flex', justifyContent: 'center' }}
                    />
                )}
            </>
        );
    };

    return (
        <Container maxWidth="lg">
            <Box component="form" onSubmit={handleSearch} sx={{ mb: 4 }}>
                <TextField
                    fullWidth
                    placeholder="Search users, articles, notifications..."
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <SearchIcon />
                            </InputAdornment>
                        ),
                    }}
                    sx={{ backgroundColor: 'white' }}
                />
            </Box>

            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                    <CircularProgress />
                </Box>
            ) : results ? (
                <>
                    <Typography variant="h5" gutterBottom>
                        Search Results for "{searchParams.get('q')}"
                    </Typography>

                    <Tabs value={tabValue} onChange={(e, v) => setTabValue(v)} sx={{ mb: 3 }}>
                        <Tab
                            label={`Users (${results.users?.totalElements || 0})`}
                            icon={<PersonIcon />}
                            iconPosition="start"
                        />
                        <Tab
                            label={`Articles (${results.articles?.totalElements || 0})`}
                            icon={<ArticleIcon />}
                            iconPosition="start"
                        />
                        <Tab
                            label={`Notifications (${results.notifications?.totalElements || 0})`}
                            icon={<NotificationIcon />}
                            iconPosition="start"
                        />
                    </Tabs>

                    <Box>
                        {tabValue === 0 && renderUsers()}
                        {tabValue === 1 && renderArticles()}
                        {tabValue === 2 && renderNotifications()}
                    </Box>
                </>
            ) : (
                <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography variant="h6" color="textSecondary">
                        Enter a search query to find users, articles, and notifications
                    </Typography>
                </Box>
            )}
        </Container>
    );
}

export default Search;
