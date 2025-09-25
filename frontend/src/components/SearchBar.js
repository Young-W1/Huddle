import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    TextField,
    InputAdornment,
    IconButton,
    Paper,
    List,
    ListItem,
    ListItemText,
    ListItemIcon,
    Typography,
    Box,
    Divider,
    CircularProgress,
    ClickAwayListener,
    Chip
} from '@mui/material';
import {
    Search as SearchIcon,
    Clear as ClearIcon,
    Person as PersonIcon,
    Article as ArticleIcon,
    Notifications as NotificationIcon
} from '@mui/icons-material';
import axios from 'axios';

function SearchBar() {
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState(null);
    const [loading, setLoading] = useState(false);
    const [showResults, setShowResults] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const delayDebounceFn = setTimeout(() => {
            if (searchQuery.trim().length > 2) {
                performSearch();
            } else {
                setSearchResults(null);
                setShowResults(false);
            }
        }, 300);

        return () => clearTimeout(delayDebounceFn);
    }, [searchQuery]);

    const performSearch = async () => {
        setLoading(true);
        try {
            const response = await axios.get('/huddle/search/global', {
                params: { query: searchQuery, size: 5 }
            });
            setSearchResults(response.data);
            setShowResults(true);
            setLoading(false);
        } catch (err) {
            console.error('Search failed:', err);
            setLoading(false);
        }
    };

    const handleClear = () => {
        setSearchQuery('');
        setSearchResults(null);
        setShowResults(false);
    };

    const handleClickAway = () => {
        setShowResults(false);
    };

    const handleUserClick = (userId) => {
        navigate(`/profile/${userId}`);
        setShowResults(false);
        setSearchQuery('');
    };

    const handleArticleClick = (articleId) => {
        navigate(`/articles/${articleId}`);
        setShowResults(false);
        setSearchQuery('');
    };

    const handleNotificationClick = () => {
        navigate('/notifications');
        setShowResults(false);
        setSearchQuery('');
    };

    return (
        <ClickAwayListener onClickAway={handleClickAway}>
            <Box sx={{ position: 'relative', width: '100%', maxWidth: 500 }}>
                <TextField
                    fullWidth
                    placeholder="Search users, articles, notifications..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <SearchIcon />
                            </InputAdornment>
                        ),
                        endAdornment: (
                            <InputAdornment position="end">
                                {loading ? (
                                    <CircularProgress size={20} />
                                ) : searchQuery && (
                                    <IconButton size="small" onClick={handleClear}>
                                        <ClearIcon />
                                    </IconButton>
                                )}
                            </InputAdornment>
                        ),
                    }}
                    size="small"
                    sx={{ backgroundColor: 'white', borderRadius: 1 }}
                />

                {showResults && searchResults && (
                    <Paper
                        sx={{
                            position: 'absolute',
                            top: '100%',
                            left: 0,
                            right: 0,
                            mt: 1,
                            maxHeight: 400,
                            overflow: 'auto',
                            zIndex: 1000
                        }}
                        elevation={3}
                    >
                        {/* Users Section */}
                        {searchResults.users?.content?.length > 0 && (
                            <>
                                <Box sx={{ p: 1, backgroundColor: '#f5f5f5' }}>
                                    <Typography variant="subtitle2" color="textSecondary">
                                        Users
                                    </Typography>
                                </Box>
                                <List dense>
                                    {searchResults.users.content.map((user) => (
                                        <ListItem
                                            button
                                            key={user.id}
                                            onClick={() => handleUserClick(user.id)}
                                        >
                                            <ListItemIcon>
                                                <PersonIcon />
                                            </ListItemIcon>
                                            <ListItemText
                                                primary={user.displayName || user.username}
                                                secondary={`@${user.username}`}
                                            />
                                        </ListItem>
                                    ))}
                                </List>
                                <Divider />
                            </>
                        )}

                        {/* Articles Section */}
                        {searchResults.articles?.content?.length > 0 && (
                            <>
                                <Box sx={{ p: 1, backgroundColor: '#f5f5f5' }}>
                                    <Typography variant="subtitle2" color="textSecondary">
                                        Articles
                                    </Typography>
                                </Box>
                                <List dense>
                                    {searchResults.articles.content.map((article) => (
                                        <ListItem
                                            button
                                            key={article.id}
                                            onClick={() => handleArticleClick(article.id)}
                                        >
                                            <ListItemIcon>
                                                <ArticleIcon />
                                            </ListItemIcon>
                                            <ListItemText
                                                primary={article.title}
                                                secondary={
                                                    <Box>
                                                        <Typography variant="caption">
                                                            by {article.authorUsername}
                                                        </Typography>
                                                        {article.tags?.length > 0 && (
                                                            <Box sx={{ mt: 0.5 }}>
                                                                {article.tags.slice(0, 2).map((tag, idx) => (
                                                                    <Chip
                                                                        key={idx}
                                                                        label={tag}
                                                                        size="small"
                                                                        sx={{ mr: 0.5, height: 20 }}
                                                                    />
                                                                ))}
                                                            </Box>
                                                        )}
                                                    </Box>
                                                }
                                            />
                                        </ListItem>
                                    ))}
                                </List>
                                <Divider />
                            </>
                        )}

                        {/* Notifications Section */}
                        {searchResults.notifications?.content?.length > 0 && (
                            <>
                                <Box sx={{ p: 1, backgroundColor: '#f5f5f5' }}>
                                    <Typography variant="subtitle2" color="textSecondary">
                                        Notifications
                                    </Typography>
                                </Box>
                                <List dense>
                                    {searchResults.notifications.content.map((notification) => (
                                        <ListItem
                                            button
                                            key={notification.id}
                                            onClick={handleNotificationClick}
                                        >
                                            <ListItemIcon>
                                                <NotificationIcon />
                                            </ListItemIcon>
                                            <ListItemText
                                                primary={notification.message}
                                                secondary={new Date(notification.createdAt).toLocaleDateString()}
                                            />
                                        </ListItem>
                                    ))}
                                </List>
                            </>
                        )}

                        {/* No Results */}
                        {(!searchResults.users?.content?.length &&
                            !searchResults.articles?.content?.length &&
                            !searchResults.notifications?.content?.length) && (
                            <Box sx={{ p: 2, textAlign: 'center' }}>
                                <Typography color="textSecondary">
                                    No results found for "{searchQuery}"
                                </Typography>
                            </Box>
                        )}
                    </Paper>
                )}
            </Box>
        </ClickAwayListener>
    );
}

export default SearchBar;
