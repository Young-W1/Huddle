import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
    Container,
    Paper,
    Typography,
    Box,
    Avatar,
    Button,
    Grid,
    Card,
    CardContent,
    Tabs,
    Tab,
    List,
    ListItem,
    ListItemAvatar,
    ListItemText,
    TextField,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Alert,
    Chip,
    Divider,
    Stack,
    useTheme,
    alpha,
    Skeleton,
    CircularProgress,
    IconButton
} from '@mui/material';
import {
    Person,
    Edit,
    Email,
    CalendarToday,
    Article,
    People,
    PersonAdd,
    PersonRemove,
    LocationOn,
    Link as LinkIcon,
    CameraAlt
} from '@mui/icons-material';
import axios from 'axios';

function Profile() {
    const { userId } = useParams();
    const navigate = useNavigate();
    const theme = useTheme();

    // State management
    const [profile, setProfile] = useState(null);
    const [articles, setArticles] = useState([]);
    const [followers, setFollowers] = useState([]);
    const [following, setFollowing] = useState([]);
    const [tabValue, setTabValue] = useState(0);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [editDialog, setEditDialog] = useState(false);
    const [editData, setEditData] = useState({
        bio: '',
        location: '',
        website: '',
        profilePicture: ''
    });
    const [isOwnProfile, setIsOwnProfile] = useState(false);
    const [isFollowing, setIsFollowing] = useState(false);

    const currentUsername = localStorage.getItem('username');

    useEffect(() => {
        if (userId === 'me') {
            fetchCurrentUserProfile();
        } else if (userId) {
            fetchProfile(userId);
        }
    }, [userId]);

    const fetchCurrentUserProfile = async () => {
        try {
            setLoading(true);
            setError('');

            const response = await axios.get('/huddle/me');

            if (response.data.success && response.data.data) {
                const profileData = response.data.data;

                setProfile({
                    userId: profileData.userId,
                    username: profileData.username,
                    email: profileData.email,
                    bio: profileData.bio || '',
                    location: profileData.location || '',
                    website: profileData.website || '',
                    profilePicture: profileData.profilePicture || '',
                    joinedDate: profileData.joinedDate,
                    followersCount: profileData.followersCount || 0,
                    followingCount: profileData.followingCount || 0,
                    articlesCount: profileData.articlesCount || 0,
                    isFollowing: false
                });

                setIsOwnProfile(true);

                setEditData({
                    bio: profileData.bio || '',
                    location: profileData.location || '',
                    website: profileData.website || '',
                    profilePicture: profileData.profilePicture || ''
                });

                if (profileData.username) {
                    fetchUserArticles(profileData.username);
                }

                if (profileData.userId) {
                    fetchFollowers(profileData.userId);
                    fetchFollowing(profileData.userId);
                }
            }
        } catch (err) {
            console.error('Failed to fetch current user profile:', err);
            setError('Failed to load profile. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const fetchProfile = async (profileId) => {
        try {
            setLoading(true);
            setError('');

            const response = await axios.get(`/huddle/users/${profileId}/profile`);

            if (response.data) {
                const profileData = response.data;

                setProfile({
                    userId: profileData.userId,
                    username: profileData.username,
                    email: profileData.email,
                    bio: profileData.bio || '',
                    location: profileData.location || '',
                    website: profileData.website || '',
                    profilePicture: profileData.profilePicture || '',
                    joinedDate: profileData.joinedDate,
                    followersCount: profileData.followersCount || 0,
                    followingCount: profileData.followingCount || 0,
                    articlesCount: profileData.articlesCount || 0,
                    isFollowing: profileData.isFollowing || false
                });

                setIsOwnProfile(profileData.username === currentUsername);
                setIsFollowing(profileData.isFollowing || false);

                setEditData({
                    bio: profileData.bio || '',
                    location: profileData.location || '',
                    website: profileData.website || '',
                    profilePicture: profileData.profilePicture || ''
                });

                if (profileData.username) {
                    fetchUserArticles(profileData.username);
                }

                fetchFollowers(profileId);
                fetchFollowing(profileId);
            }
        } catch (err) {
            console.error('Failed to fetch profile:', err);
            setError('Failed to load profile. User may not exist.');
        } finally {
            setLoading(false);
        }
    };

    const fetchUserArticles = async (username) => {
        try {
            const response = await axios.get('/huddle/articles/search', {
                params: {
                    authorUsername: username,
                    page: 0,
                    size: 20,
                    sort: 'createdAt,desc'
                }
            });

            if (response.data.success && response.data.data) {
                setArticles(response.data.data.content || []);
            }
        } catch (err) {
            console.error('Failed to fetch articles:', err);
            setArticles([]);
        }
    };

    const fetchFollowers = async (profileId) => {
        try {
            const response = await axios.get(`/huddle/users/${profileId}/followers`, {
                params: { page: 0, size: 100 }
            });

            if (response.data) {
                setFollowers(response.data.content || []);
                if (response.data.totalElements !== undefined) {
                    setProfile(prev => ({
                        ...prev,
                        followersCount: response.data.totalElements
                    }));
                }
            }
        } catch (err) {
            console.error('Failed to fetch followers:', err);
            setFollowers([]);
        }
    };

    const fetchFollowing = async (profileId) => {
        try {
            const response = await axios.get(`/huddle/users/${profileId}/following`, {
                params: { page: 0, size: 100 }
            });

            if (response.data) {
                setFollowing(response.data.content || []);
                if (response.data.totalElements !== undefined) {
                    setProfile(prev => ({
                        ...prev,
                        followingCount: response.data.totalElements
                    }));
                }
            }
        } catch (err) {
            console.error('Failed to fetch following:', err);
            setFollowing([]);
        }
    };

    const handleFollow = async () => {
        if (!profile?.userId) return;

        try {
            const response = await axios.post(`/huddle/users/${profile.userId}/follow`);
            if (response.data.success) {
                setIsFollowing(true);
                setProfile(prev => ({
                    ...prev,
                    followersCount: (prev.followersCount || 0) + 1
                }));
                fetchFollowers(profile.userId);
            }
        } catch (err) {
            console.error('Failed to follow user:', err);
            alert('Failed to follow user');
        }
    };

    const handleUnfollow = async () => {
        if (!profile?.userId) return;

        try {
            const response = await axios.delete(`/huddle/users/${profile.userId}/unfollow`);
            if (response.data.success) {
                setIsFollowing(false);
                setProfile(prev => ({
                    ...prev,
                    followersCount: Math.max(0, (prev.followersCount || 0) - 1)
                }));
                fetchFollowers(profile.userId);
            }
        } catch (err) {
            console.error('Failed to unfollow user:', err);
            alert('Failed to unfollow user');
        }
    };

    const handleUpdateProfile = async () => {
        try {
            const response = await axios.put('/huddle/users/profile', editData);
            if (response.data.success) {
                setProfile(prev => ({
                    ...prev,
                    ...editData
                }));
                setEditDialog(false);
                alert('Profile updated successfully');
            }
        } catch (err) {
            console.error('Failed to update profile:', err);
            alert('Failed to update profile');
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
            <Container maxWidth="lg" sx={{ mt: 4 }}>
                <Alert severity="error">{error}</Alert>
                <Button onClick={() => navigate(-1)} sx={{ mt: 2 }}>
                    Go Back
                </Button>
            </Container>
        );
    }

    if (!profile) {
        return (
            <Container maxWidth="lg" sx={{ mt: 4 }}>
                <Alert severity="warning">Profile not found</Alert>
                <Button onClick={() => navigate('/')} sx={{ mt: 2 }}>
                    Go to Home
                </Button>
            </Container>
        );
    }

    return (
        <>
            {/* Profile Header Section */}
            <Box
                sx={{
                    height: 200,
                    background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
                    position: 'relative'
                }}
            />

            {/* Main Content */}
            <Container maxWidth="lg" sx={{ mt: -8 }}>
                <Grid container spacing={3}>
                    <Grid item xs={12}>
                        <Paper sx={{ p: 3, borderRadius: 2 }}>
                            {/* Avatar and User Info Row */}
                            <Box
                                sx={{
                                    display: 'flex',
                                    flexDirection: { xs: 'column', md: 'row' },
                                    alignItems: { xs: 'center', md: 'flex-start' },
                                    gap: 3,
                                    mb: 3
                                }}
                            >
                                {/* Avatar */}
                                <Avatar
                                    src={profile.profilePicture}
                                    sx={{
                                        width: 150,
                                        height: 150,
                                        border: `4px solid ${theme.palette.background.paper}`,
                                        backgroundColor: theme.palette.primary.main,
                                        fontSize: '3rem',
                                        boxShadow: theme.shadows[3],
                                        mt: -10
                                    }}
                                >
                                    {profile.username?.[0]?.toUpperCase()}
                                </Avatar>

                                {/* User Info and Actions */}
                                {/* User Info and Actions */}
                                <Box
                                    sx={{
                                        flex: 1,
                                        display: 'flex',
                                        flexDirection: 'column',
                                        alignItems: { xs: 'center', md: 'flex-start' },
                                        textAlign: { xs: 'center', md: 'left' },
                                        width: '100%'
                                    }}
                                >
                                    <Box
                                        sx={{
                                            display: 'flex',
                                            justifyContent: 'space-between',
                                            alignItems: 'flex-start',
                                            width: '100%',
                                            flexDirection: { xs: 'column', sm: 'row' },
                                            gap: 2
                                        }}
                                    >
                                        <Box>
                                            {/* Username - with explicit dark color for contrast */}
                                            <Typography
                                                variant="h3"
                                                sx={{
                                                    fontWeight: 500,
                                                    mb: 1,
                                                    color: theme.palette.text.primary,  // Explicit dark color instead of theme color
                                                    letterSpacing: '-0.5px',
                                                    // textShadow: '0px 2px 4px rgba(0,0,0,0.1)',  // Add shadow for better visibility
                                                    zIndex: 10,  // Ensure it's above other elements
                                                    position: 'relative'  // Position relative for z-index to work
                                                }}
                                            >
                                                {profile.username || 'No Username'}
                                            </Typography>

                                            {/* Email - secondary information */}
                                            {profile.email && (
                                                <Typography
                                                    variant="body1"
                                                    sx={{
                                                        color: '#666666',  // Explicit grey color
                                                        fontSize: '1rem',
                                                        fontWeight: 400,
                                                        zIndex: 10,
                                                        position: 'relative'
                                                    }}
                                                >
                                                    {profile.email}
                                                </Typography>
                                            )}
                                        </Box>

                                        <Stack direction="row" spacing={1}>
                                            {isOwnProfile ? (
                                                <Button
                                                    variant="contained"
                                                    startIcon={<Edit />}
                                                    onClick={() => setEditDialog(true)}
                                                    sx={{
                                                        backgroundColor: '#ffffff',  // Explicit white background
                                                        color: theme.palette.primary.main,
                                                        border: `2px solid ${theme.palette.primary.main}`,
                                                        fontWeight: 600,
                                                        '&:hover': {
                                                            backgroundColor: '#f5f5f5',
                                                            borderColor: theme.palette.primary.dark,
                                                            transform: 'translateY(-1px)',
                                                            boxShadow: theme.shadows[4]
                                                        }
                                                    }}
                                                >
                                                    Edit Profile
                                                </Button>
                                            ) : profile.userId && (
                                                isFollowing ? (
                                                    <Button
                                                        variant="outlined"
                                                        startIcon={<PersonRemove />}
                                                        onClick={handleUnfollow}
                                                        sx={{
                                                            borderColor: theme.palette.primary.main,
                                                            color: theme.palette.primary.main,
                                                            backgroundColor: '#ffffff',
                                                            fontWeight: 600,
                                                            '&:hover': {
                                                                backgroundColor: alpha(theme.palette.error.main, 0.05),
                                                                borderColor: theme.palette.error.main,
                                                                color: theme.palette.error.main
                                                            }
                                                        }}
                                                    >
                                                        Unfollow
                                                    </Button>
                                                ) : (
                                                    <Button
                                                        variant="contained"
                                                        startIcon={<PersonAdd />}
                                                        onClick={handleFollow}
                                                        sx={{
                                                            backgroundColor: theme.palette.primary.main,
                                                            color: '#ffffff',
                                                            fontWeight: 600,
                                                            '&:hover': {
                                                                backgroundColor: theme.palette.primary.dark,
                                                                transform: 'translateY(-1px)',
                                                                boxShadow: theme.shadows[4]
                                                            }
                                                        }}
                                                    >
                                                        Follow
                                                    </Button>
                                                )
                                            )}
                                        </Stack>
                                    </Box>

                                    {/* Add a debug check to see what's in profile */}
                                    {!profile.username && (
                                        <Alert severity="warning" sx={{ mt: 2, width: '100%' }}>
                                            Username not loaded properly
                                        </Alert>
                                    )}

                                    {profile.bio && (
                                        <Typography
                                            variant="body1"
                                            sx={{
                                                mt: 2,
                                                maxWidth: '800px',
                                                color: '#333333',  // Explicit dark color
                                                zIndex: 10,
                                                position: 'relative'
                                            }}
                                        >
                                            {profile.bio}
                                        </Typography>
                                    )}

                                    <Stack
                                        direction="row"
                                        spacing={3}
                                        flexWrap="wrap"
                                        sx={{ mt: 2 }}
                                    >
                                        {profile.location && (
                                            <Box display="flex" alignItems="center" gap={0.5}>
                                                <LocationOn fontSize="small" color="action" />
                                                <Typography variant="body2">{profile.location}</Typography>
                                            </Box>
                                        )}
                                        {profile.website && (
                                            <Box display="flex" alignItems="center" gap={0.5}>
                                                <LinkIcon fontSize="small" color="action" />
                                                <Typography
                                                    variant="body2"
                                                    component="a"
                                                    href={profile.website}
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    sx={{ color: 'primary.main', textDecoration: 'none' }}
                                                >
                                                    {profile.website}
                                                </Typography>
                                            </Box>
                                        )}
                                        <Box display="flex" alignItems="center" gap={0.5}>
                                            <CalendarToday fontSize="small" color="action" />
                                            <Typography variant="body2">
                                                Joined {new Date(profile.joinedDate || Date.now()).toLocaleDateString('en-US', {
                                                month: 'long',
                                                year: 'numeric'
                                            })}
                                            </Typography>
                                        </Box>
                                    </Stack>
                                </Box>
                            </Box>

                            <Divider sx={{ my: 3 }} />

                            {/* Stats */}
                            <Grid container spacing={3} sx={{ mb: 3 }}>
                                <Grid item xs={4}>
                                    <Box textAlign="center">
                                        <Typography variant="h5" sx={{ fontWeight: 600 }}>
                                            {profile.articlesCount || 0}
                                        </Typography>
                                        <Typography variant="body2" color="textSecondary">
                                            Articles
                                        </Typography>
                                    </Box>
                                </Grid>
                                <Grid item xs={4}>
                                    <Box
                                        textAlign="center"
                                        sx={{ cursor: 'pointer' }}
                                        onClick={() => setTabValue(1)}
                                    >
                                        <Typography variant="h5" sx={{ fontWeight: 600 }}>
                                            {profile.followersCount || 0}
                                        </Typography>
                                        <Typography variant="body2" color="textSecondary">
                                            Followers
                                        </Typography>
                                    </Box>
                                </Grid>
                                <Grid item xs={4}>
                                    <Box
                                        textAlign="center"
                                        sx={{ cursor: 'pointer' }}
                                        onClick={() => setTabValue(2)}
                                    >
                                        <Typography variant="h5" sx={{ fontWeight: 600 }}>
                                            {profile.followingCount || 0}
                                        </Typography>
                                        <Typography variant="body2" color="textSecondary">
                                            Following
                                        </Typography>
                                    </Box>
                                </Grid>
                            </Grid>
                        </Paper>

                        {/* Content Tabs */}
                        <Paper sx={{ mt: 3 }}>
                            <Tabs
                                value={tabValue}
                                onChange={(e, v) => setTabValue(v)}
                                variant="fullWidth"
                            >
                                <Tab label="Articles" icon={<Article />} iconPosition="start" />
                                <Tab label="Followers" icon={<People />} iconPosition="start" />
                                <Tab label="Following" icon={<People />} iconPosition="start" />
                            </Tabs>

                            <Box sx={{ p: 3, minHeight: 400 }}>
                                {tabValue === 0 && (
                                    <Grid container spacing={2}>
                                        {articles.length > 0 ? articles.map((article) => (
                                            <Grid item xs={12} key={article.id}>
                                                <Card
                                                    sx={{
                                                        cursor: 'pointer',
                                                        transition: 'all 0.3s',
                                                        '&:hover': {
                                                            transform: 'translateY(-2px)',
                                                            boxShadow: 3
                                                        }
                                                    }}
                                                    onClick={() => navigate(`/articles/${article.id}`)}
                                                >
                                                    <CardContent>
                                                        <Typography variant="h6" gutterBottom>
                                                            {article.title}
                                                        </Typography>
                                                        <Typography
                                                            variant="body2"
                                                            color="textSecondary"
                                                            paragraph
                                                        >
                                                            {article.content?.substring(0, 200)}...
                                                        </Typography>
                                                        <Typography variant="caption" color="textSecondary">
                                                            {new Date(article.createdAt).toLocaleDateString()}
                                                        </Typography>
                                                    </CardContent>
                                                </Card>
                                            </Grid>
                                        )) : (
                                            <Box sx={{ py: 4, textAlign: 'center', width: '100%' }}>
                                                <Article sx={{ fontSize: 48, color: 'text.secondary', mb: 2 }} />
                                                <Typography color="textSecondary">
                                                    No articles yet
                                                </Typography>
                                            </Box>
                                        )}
                                    </Grid>
                                )}

                                {tabValue === 1 && (
                                    <List>
                                        {followers.length > 0 ? followers.map((follower) => (
                                            <ListItem
                                                key={follower.userId}
                                                button
                                                onClick={() => navigate(`/profile/${follower.userId}`)}
                                                sx={{
                                                    borderRadius: 1,
                                                    mb: 1,
                                                    '&:hover': { backgroundColor: alpha(theme.palette.primary.main, 0.05) }
                                                }}
                                            >
                                                <ListItemAvatar>
                                                    <Avatar src={follower.profilePicture}>
                                                        {follower.username?.[0]?.toUpperCase()}
                                                    </Avatar>
                                                </ListItemAvatar>
                                                <ListItemText
                                                    primary={follower.username}
                                                    secondary={follower.bio}
                                                />
                                            </ListItem>
                                        )) : (
                                            <Box sx={{ py: 4, textAlign: 'center' }}>
                                                <People sx={{ fontSize: 48, color: 'text.secondary', mb: 2 }} />
                                                <Typography color="textSecondary">
                                                    No followers yet
                                                </Typography>
                                            </Box>
                                        )}
                                    </List>
                                )}

                                {tabValue === 2 && (
                                    <List>
                                        {following.length > 0 ? following.map((user) => (
                                            <ListItem
                                                key={user.userId}
                                                button
                                                onClick={() => navigate(`/profile/${user.userId}`)}
                                                sx={{
                                                    borderRadius: 1,
                                                    mb: 1,
                                                    '&:hover': { backgroundColor: alpha(theme.palette.primary.main, 0.05) }
                                                }}
                                            >
                                                <ListItemAvatar>
                                                    <Avatar src={user.profilePicture}>
                                                        {user.username?.[0]?.toUpperCase()}
                                                    </Avatar>
                                                </ListItemAvatar>
                                                <ListItemText
                                                    primary={user.username}
                                                    secondary={user.bio}
                                                />
                                            </ListItem>
                                        )) : (
                                            <Box sx={{ py: 4, textAlign: 'center' }}>
                                                <PersonAdd sx={{ fontSize: 48, color: 'text.secondary', mb: 2 }} />
                                                <Typography color="textSecondary">
                                                    Not following anyone yet
                                                </Typography>
                                            </Box>
                                        )}
                                    </List>
                                )}
                            </Box>
                        </Paper>
                    </Grid>
                </Grid>
            </Container>

            {/* Edit Profile Dialog */}
            <Dialog open={editDialog} onClose={() => setEditDialog(false)} maxWidth="sm" fullWidth>
                <DialogTitle>Edit Profile</DialogTitle>
                <DialogContent>
                    <Stack spacing={3} sx={{ mt: 2 }}>
                        <TextField
                            fullWidth
                            label="Bio"
                            multiline
                            rows={4}
                            value={editData.bio}
                            onChange={(e) => setEditData({...editData, bio: e.target.value})}
                        />
                        <TextField
                            fullWidth
                            label="Location"
                            value={editData.location}
                            onChange={(e) => setEditData({...editData, location: e.target.value})}
                        />
                        <TextField
                            fullWidth
                            label="Website"
                            value={editData.website}
                            onChange={(e) => setEditData({...editData, website: e.target.value})}
                        />
                        <TextField
                            fullWidth
                            label="Profile Picture URL"
                            value={editData.profilePicture}
                            onChange={(e) => setEditData({...editData, profilePicture: e.target.value})}
                        />
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setEditDialog(false)}>Cancel</Button>
                    <Button onClick={handleUpdateProfile} variant="contained">
                        Save Changes
                    </Button>
                </DialogActions>
            </Dialog>
        </>
    );
}

export default Profile;