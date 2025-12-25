import React, { useState, useEffect, useRef } from 'react';
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
    const { username } = useParams(); // Changed from userId to username
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
    const [uploading, setUploading] = useState(false);
    const fileInputRef = useRef(null);

    const currentUsername = localStorage.getItem('username');

    useEffect(() => {
        // Debug logging
        console.log('Profile useEffect - username from URL:', username);
        console.log('Profile useEffect - current username:', currentUsername);

        if (!username) {
            setError('No username provided');
            setLoading(false);
            return;
        }

        // Reset states when username changes
        setLoading(true);
        setError('');

        // Fetch the profile
        if (username === 'me' || username === currentUsername) {
            console.log('Fetching current user profile');
            fetchCurrentUserProfile();
        } else {
            console.log('Fetching profile for:', username);
            fetchProfile(username);
        }
    }, [username]); // Remove currentUsername from dependencies


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

    const fetchProfile = async (profileUsername) => {
        try {
            setLoading(true);
            setError('');

            const response = await axios.get(`/huddle/users/${profileUsername}/profile`);

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

                if (profileData.userId) {
                    fetchFollowers(profileData.userId);
                    fetchFollowing(profileData.userId);
                }
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

    const handleProfilePictureUpload = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        if (!file.type.startsWith('image/')) {
            alert('Please select an image file');
            return;
        }

        if (file.size > 5 * 1024 * 1024) {
            alert('File size must be less than 5MB');
            return;
        }

        setUploading(true);
        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await axios.post('/huddle/users/profile/picture', formData, {
                headers: {
                    'Content-Type': 'multipart/form-data',
                },
            });

            if (response.data.success) {
                setProfile(prev => ({
                    ...prev,
                    profilePicture: response.data.data
                }));
                setEditData(prev => ({
                    ...prev,
                    profilePicture: response.data.data
                }));
                alert('Profile picture uploaded successfully');
            }
        } catch (err) {
            console.error('Failed to upload profile picture:', err);
            alert('Failed to upload profile picture');
        } finally {
            setUploading(false);
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
                alert('Profile updated successfully');
            }
        } catch (err) {
            console.error('Failed to update profile:', err);
            alert('Failed to update profile');
        } finally {
            setEditDialog(false);
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
                                {/* Avatar with upload button */}
                                <Box position="relative">
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
                                    {isOwnProfile && (
                                        <>
                                            <input
                                                ref={fileInputRef}
                                                type="file"
                                                accept="image/*"
                                                style={{ display: 'none' }}
                                                onChange={handleProfilePictureUpload}
                                                disabled={uploading}
                                            />
                                            <IconButton
                                                sx={{
                                                    position: 'absolute',
                                                    bottom: 0,
                                                    right: 0,
                                                    backgroundColor: theme.palette.background.paper,
                                                    '&:hover': {
                                                        backgroundColor: theme.palette.action.hover,
                                                    },
                                                    boxShadow: theme.shadows[2],
                                                }}
                                                onClick={() => fileInputRef.current?.click()}
                                                disabled={uploading}
                                            >
                                                {uploading ? (
                                                    <CircularProgress size={20} />
                                                ) : (
                                                    <CameraAlt />
                                                )}
                                            </IconButton>
                                        </>
                                    )}
                                </Box>

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
                                            <Typography
                                                variant="h3"
                                                sx={{
                                                    fontWeight: 500,
                                                    mb: 1,
                                                    color: theme.palette.text.primary,
                                                    letterSpacing: '-0.5px',
                                                    zIndex: 10,
                                                    position: 'relative'
                                                }}
                                            >
                                                {profile.username || 'No Username'}
                                            </Typography>

                                            {profile.email && (
                                                <Typography
                                                    variant="body1"
                                                    sx={{
                                                        color: '#666666',
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
                                                        backgroundColor: '#ffffff',
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

                                    {profile.bio && (
                                        <Typography
                                            variant="body1"
                                            sx={{
                                                mt: 2,
                                                maxWidth: '800px',
                                                color: '#333333',
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
                                                onClick={() => navigate(`/profile/${follower.username}`)}
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
                                                onClick={() => navigate(`/profile/${user.username}`)}
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
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                            <Avatar
                                src={editData.profilePicture}
                                sx={{ width: 80, height: 80 }}
                            >
                                {profile.username?.[0]?.toUpperCase()}
                            </Avatar>
                            <Typography variant="body2" color="text.secondary">
                                Use the camera icon on your avatar to upload a new profile picture
                            </Typography>
                        </Box>
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