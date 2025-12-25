import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Container,
    Grid,
    Card,
    CardContent,
    CardActions,
    Typography,
    Button,
    Box,
    Chip
} from '@mui/material';
import { Visibility, ThumbUp } from '@mui/icons-material';
import axios from 'axios';

function Home() {
    const [recentArticles, setRecentArticles] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        fetchRecentArticles();
    }, []);

    const fetchRecentArticles = async () => {
        try {
            const response = await axios.get('/huddle/articles/allArticles?size=6');
            if (response.data.success) {
                setRecentArticles(response.data.data.content || []);
            }
        } catch (err) {
            console.error('Failed to fetch articles:', err);
        }
    };

    return (
        <Container maxWidth="lg">
            <Box sx={{ mb: 4 }}>
                <Typography variant="h3" gutterBottom>
                    Welcome to Huddle
                </Typography>
                <Typography variant="h6" color="textSecondary">
                    Discover, Share, and Engage with Articles
                </Typography>
            </Box>

            // Add at the top of the Home component after the welcome section:
            <Paper sx={{ p: 3, mb: 4, backgroundColor: '#f5f5f5' }}>
                <Typography variant="h6" gutterBottom>
                    Find what you're looking for
                </Typography>
                <TextField
                    fullWidth
                    placeholder="Search users, articles, notifications..."
                    onKeyPress={(e) => {
                        if (e.key === 'Enter') {
                            navigate(`/search?q=${e.target.value}`);
                        }
                    }}
                    InputProps={{
                        startAdornment: (
                            <InputAdornment position="start">
                                <SearchIcon />
                            </InputAdornment>
                        ),
                    }}
                />
            </Paper>

            <Typography variant="h5" gutterBottom sx={{ mt: 4, mb: 2 }}>
                Recent Articles
            </Typography>

            <Grid container spacing={3}>
                {recentArticles.map((article) => (
                    <Grid item xs={12} sm={6} md={4} key={article.id}>
                        <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                            <CardContent sx={{ flexGrow: 1 }}>
                                <Typography gutterBottom variant="h6" component="h2">
                                    {article.title}
                                </Typography>
                                <Typography variant="body2" color="textSecondary" paragraph>
                                    {article.content?.substring(0, 150)}...
                                </Typography>
                                <Box sx={{ mt: 1 }}>
                                    {article.tags?.slice(0, 3).map((tag, index) => (
                                        <Chip key={index} label={tag} size="small" sx={{ mr: 0.5 }} />
                                    ))}
                                </Box>
                            </CardContent>
                            <CardActions>
                                <Button size="small" onClick={() => navigate(`/articles/${article.id}`)}>
                                    Read More
                                </Button>
                                <Box sx={{ ml: 'auto', display: 'flex', alignItems: 'center', gap: 1 }}>
                                    <Visibility fontSize="small" />
                                    <Typography variant="body2">{article.viewCount || 0}</Typography>
                                </Box>
                            </CardActions>
                        </Card>
                    </Grid>
                ))}
            </Grid>

            {recentArticles.length === 0 && (
                <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography variant="h6" color="textSecondary">
                        No articles yet. Be the first to create one!
                    </Typography>
                    <Button
                        variant="contained"
                        sx={{ mt: 2 }}
                        onClick={() => navigate('/articles')}
                    >
                        Create Article
                    </Button>
                </Box>
            )}
        </Container>
    );
}

export default Home;
