import React, { useState, useEffect } from 'react';
import {
    Container,
    Paper,
    Box,
    Avatar,
    Typography,
    CircularProgress,
    Alert,
    Divider,
    Grid,
    Card,
    CardContent,
    Button,
    Stack
} from '@mui/material';
import {
    Email,
    LocationOn,
    Language,
    CalendarToday,
    Article,
    People,
    PersonAdd
} from '@mui/icons-material';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function UserProfile() {
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        // Check if axios has default authorization header
        console.log('Axios defaults:', axios.defaults.headers);

        // Check localStorage for token
        const token = localStorage.getItem('token');
        console.log('Token from localStorage:', token);

        // Check cookies
        console.log('Document cookies:', document.cookie);

        fetchProfile();
    }, []);

    const fetchProfile = async () => {
        try {
            // Log the request details
            console.log('Making request to /huddle/me');
            console.log('Request headers:', axios.defaults.headers);

            const response = await axios.get('/huddle/me', {
                withCredentials: true, // Ensure cookies are sent
                headers: {
                    // Explicitly set authorization if token exists
                    ...(localStorage.getItem('token') && {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    })
                }
            });

            console.log('Profile response:', response.data);

            if (response.data.success) {
                setProfile(response.data.data);
            } else {
                setError(response.data.message || 'Failed to load profile');
            }
        } catch (err) {
            console.error('Error fetching profile:', err);
            console.error('Error response:', err.response);
            console.error('Error status:', err.response?.status);
            console.error('Error data:', err.response?.data);

            if (err.response?.status === 401) {
                // User is not authenticated
                console.log('User not authenticated, redirecting to login');
                navigate('/login');
            } else if (err.response?.status === 403) {
                setError('Access denied. Please check your permissions.');
            } else if (err.response?.status === 404) {
                setError('Profile endpoint not found. Please check the API.');
            } else {
                setError(err.response?.data?.message || `Failed to load profile: ${err.message}`);
            }
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
                <CircularProgress />
            </Box>
        );
    }

    if (error) {
        return (
            <Container maxWidth="md" sx={{ mt: 4 }}>
                <Alert severity="error">{error}</Alert>
                <Button
                    sx={{ mt: 2 }}
                    variant="contained"
                    onClick={() => navigate('/login')}
                >
                    Go to Login
                </Button>
            </Container>
        );
    }

    if (!profile) {
        return (
            <Container maxWidth="md" sx={{ mt: 4 }}>
                <Alert severity="info">No profile data available</Alert>
                <Button
                    sx={{ mt: 2 }}
                    variant="contained"
                    onClick={() => window.location.reload()}
                >
                    Retry
                </Button>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }}>
            <Grid container spacing={3}>
                <Grid item xs={12} md={4}>
                    <Paper sx={{ p: 3 }}>
                        <Box display="flex" flexDirection="column" alignItems="center">
                            <Avatar
                                src={profile.profilePicture}
                                sx={{ width: 120, height: 120, mb: 2 }}
                            >
                                {profile.username?.charAt(0).toUpperCase()}
                            </Avatar>
                            <Typography variant="h5" fontWeight="bold" gutterBottom>
                                {profile.username}
                            </Typography>
                            <Typography variant="body2" color="textSecondary" gutterBottom>
                                @{profile.username}
                            </Typography>

                            {profile.bio && (
                                <Typography variant="body1" sx={{ mt: 2, textAlign: 'center' }}>
                                    {profile.bio}
                                </Typography>
                            )}

                            <Divider sx={{ width: '100%', my: 2 }} />

                            <Stack spacing={1} width="100%">
                                <Box display="flex" alignItems="center" gap={1}>
                                    <Email fontSize="small" color="action" />
                                    <Typography variant="body2">{profile.email}</Typography>
                                </Box>

                                {profile.location && (
                                    <Box display="flex" alignItems="center" gap={1}>
                                        <LocationOn fontSize="small" color="action" />
                                        <Typography variant="body2">{profile.location}</Typography>
                                    </Box>
                                )}

                                {profile.website && (
                                    <Box display="flex" alignItems="center" gap={1}>
                                        <Language fontSize="small" color="action" />
                                        <Typography variant="body2">{profile.website}</Typography>
                                    </Box>
                                )}

                                <Box display="flex" alignItems="center" gap={1}>
                                    <CalendarToday fontSize="small" color="action" />
                                    <Typography variant="body2">
                                        Joined {new Date(profile.joinedDate).toLocaleDateString()}
                                    </Typography>
                                </Box>
                            </Stack>

                            <Button
                                variant="outlined"
                                fullWidth
                                sx={{ mt: 3 }}
                                onClick={() => navigate('/profile/me')}
                            >
                                Edit Profile
                            </Button>
                        </Box>
                    </Paper>
                </Grid>

                <Grid item xs={12} md={8}>
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={4}>
                            <Card>
                                <CardContent>
                                    <Box display="flex" alignItems="center" justifyContent="space-between">
                                        <Box>
                                            <Typography color="textSecondary" variant="body2">
                                                Articles
                                            </Typography>
                                            <Typography variant="h4">
                                                {profile.articlesCount || 0}
                                            </Typography>
                                        </Box>
                                        <Article color="primary" />
                                    </Box>
                                </CardContent>
                            </Card>
                        </Grid>

                        <Grid item xs={12} sm={4}>
                            <Card>
                                <CardContent>
                                    <Box display="flex" alignItems="center" justifyContent="space-between">
                                        <Box>
                                            <Typography color="textSecondary" variant="body2">
                                                Followers
                                            </Typography>
                                            <Typography variant="h4">
                                                {profile.followersCount || 0}
                                            </Typography>
                                        </Box>
                                        <People color="primary" />
                                    </Box>
                                </CardContent>
                            </Card>
                        </Grid>

                        <Grid item xs={12} sm={4}>
                            <Card>
                                <CardContent>
                                    <Box display="flex" alignItems="center" justifyContent="space-between">
                                        <Box>
                                            <Typography color="textSecondary" variant="body2">
                                                Following
                                            </Typography>
                                            <Typography variant="h4">
                                                {profile.followingCount || 0}
                                            </Typography>
                                        </Box>
                                        <PersonAdd color="primary" />
                                    </Box>
                                </CardContent>
                            </Card>
                        </Grid>
                    </Grid>

                    <Paper sx={{ mt: 2, p: 3 }}>
                        <Typography variant="h6" gutterBottom>
                            Quick Actions
                        </Typography>
                        <Grid container spacing={2}>
                            <Grid item xs={12} sm={6}>
                                <Button
                                    variant="contained"
                                    fullWidth
                                    onClick={() => navigate('/create-article')}
                                >
                                    Create New Article
                                </Button>
                            </Grid>
                            <Grid item xs={12} sm={6}>
                                <Button
                                    variant="outlined"
                                    fullWidth
                                    onClick={() => navigate('/my-articles')}
                                >
                                    View My Articles
                                </Button>
                            </Grid>
                        </Grid>
                    </Paper>
                </Grid>
            </Grid>
        </Container>
    );
}

export default UserProfile;
