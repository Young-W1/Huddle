import React, { useState, useEffect } from 'react';
import {
    Container,
    Typography,
    Paper,
    Grid,
    Box,
    CircularProgress,
    Alert,
    Card,
    CardContent,
    List,
    ListItem,
    ListItemText,
    Divider,
    Chip,
    LinearProgress,
    Avatar,
    Stack
} from '@mui/material';
import {
    People as PeopleIcon,
    Article as ArticleIcon,
    Report as ReportIcon,
    TrendingUp as TrendingUpIcon,
    Category as CategoryIcon,
    PersonAdd as PersonAddIcon,
    Star as StarIcon
} from '@mui/icons-material';
import { analyticsService } from '../services/api';

function Analytics() {
    const [analytics, setAnalytics] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchAnalytics();
    }, []);

    const fetchAnalytics = async () => {
        try {
            const response = await analyticsService.getAllAnalytics();
            if (response.data.success) {
                setAnalytics(response.data.data);
            }
            setLoading(false);
        } catch (err) {
            setError('You need admin privileges to view analytics');
            setLoading(false);
        }
    };

    const StatCard = ({ icon, title, value, subtitle, color = 'primary' }) => (
        <Card elevation={3} sx={{ height: '100%', position: 'relative', overflow: 'visible' }}>
            <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                    <Avatar sx={{ bgcolor: `${color}.light`, mr: 2 }}>
                        {icon}
                    </Avatar>
                    <Box>
                        <Typography color="textSecondary" variant="body2">
                            {title}
                        </Typography>
                        <Typography variant="h4" fontWeight="bold">
                            {value}
                        </Typography>
                        {subtitle && (
                            <Typography variant="caption" color="textSecondary">
                                {subtitle}
                            </Typography>
                        )}
                    </Box>
                </Box>
            </CardContent>
        </Card>
    );

    if (loading) {
        return (
            <Container sx={{ display: 'flex', justifyContent: 'center', py: 8 }}>
                <CircularProgress />
            </Container>
        );
    }

    if (error) {
        return (
            <Container sx={{ py: 4 }}>
                <Alert severity="error">{error}</Alert>
            </Container>
        );
    }

    const userStats = analytics?.userStats || {};
    const postStats = analytics?.postStats || {};
    const reportStats = analytics?.reportStats || {};

    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Typography variant="h4" gutterBottom fontWeight="bold" sx={{ mb: 4 }}>
                Platform Analytics Dashboard
            </Typography>

            {/* Key Metrics Cards */}
            <Grid container spacing={3} sx={{ mb: 4 }}>
                <Grid item xs={12} sm={6} md={3}>
                    <StatCard
                        icon={<PeopleIcon />}
                        title="Total Users"
                        value={userStats.totalUsers || 0}
                        subtitle={`+${userStats.newUsersThisWeek || 0} this week`}
                        color="primary"
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <StatCard
                        icon={<ArticleIcon />}
                        title="Total Posts"
                        value={postStats.totalPosts || 0}
                        subtitle="All time"
                        color="success"
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <StatCard
                        icon={<TrendingUpIcon />}
                        title="User Growth"
                        value={`${userStats.userGrowthRate || 0}%`}
                        subtitle="This week"
                        color="info"
                    />
                </Grid>
                <Grid item xs={12} sm={6} md={3}>
                    <StatCard
                        icon={<ReportIcon />}
                        title="Reports"
                        value={reportStats.totalReports || 0}
                        subtitle={`${reportStats.pendingReports || 0} pending`}
                        color="warning"
                    />
                </Grid>
            </Grid>

            <Grid container spacing={3}>
                {/* User Analytics Section */}
                <Grid item xs={12} md={4}>
                    <Paper elevation={2} sx={{ p: 3, height: '100%' }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                            <PeopleIcon sx={{ mr: 1, color: 'primary.main' }} />
                            <Typography variant="h6" fontWeight="bold">
                                User Engagement
                            </Typography>
                        </Box>
                        <Stack spacing={2}>
                            <Box>
                                <Typography variant="body2" color="textSecondary">
                                    New Users This Week
                                </Typography>
                                <Box sx={{ display: 'flex', alignItems: 'center', mt: 1 }}>
                                    <PersonAddIcon sx={{ mr: 1, fontSize: 20, color: 'success.main' }} />
                                    <Typography variant="h5" fontWeight="bold">
                                        {userStats.newUsersThisWeek || 0}
                                    </Typography>
                                </Box>
                            </Box>
                            <Divider />
                            <Box>
                                <Typography variant="body2" color="textSecondary">
                                    Active Users (30 days)
                                </Typography>
                                <Typography variant="h6">
                                    {userStats.activeUsersLast30Days || 0}
                                </Typography>
                            </Box>
                            <Box>
                                <Typography variant="body2" color="textSecondary" gutterBottom>
                                    Growth Rate
                                </Typography>
                                <LinearProgress
                                    variant="determinate"
                                    value={Math.min(userStats.userGrowthRate || 0, 100)}
                                    sx={{ height: 8, borderRadius: 4 }}
                                />
                                <Typography variant="caption" color="textSecondary">
                                    {userStats.userGrowthRate || 0}% increase
                                </Typography>
                            </Box>
                        </Stack>
                    </Paper>
                </Grid>

                {/* Posts by Category */}
                <Grid item xs={12} md={4}>
                    <Paper elevation={2} sx={{ p: 3, height: '100%' }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                            <CategoryIcon sx={{ mr: 1, color: 'success.main' }} />
                            <Typography variant="h6" fontWeight="bold">
                                Posts by Category
                            </Typography>
                        </Box>
                        <List dense>
                            {postStats.postsByCategory && Object.entries(postStats.postsByCategory)
                                .sort(([,a], [,b]) => b - a)
                                .slice(0, 5)
                                .map(([category, count]) => (
                                    <ListItem key={category} sx={{ px: 0 }}>
                                        <ListItemText
                                            primary={category}
                                            secondary={`${count} posts`}
                                        />
                                        <Chip
                                            label={count}
                                            size="small"
                                            color="success"
                                            variant="outlined"
                                        />
                                    </ListItem>
                                ))
                            }
                            {(!postStats.postsByCategory || Object.keys(postStats.postsByCategory).length === 0) && (
                                <Typography variant="body2" color="textSecondary">
                                    No category data available
                                </Typography>
                            )}
                        </List>
                    </Paper>
                </Grid>

                {/* Top Contributors */}
                <Grid item xs={12} md={4}>
                    <Paper elevation={2} sx={{ p: 3, height: '100%' }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                            <StarIcon sx={{ mr: 1, color: 'warning.main' }} />
                            <Typography variant="h6" fontWeight="bold">
                                Top 5 Contributors
                            </Typography>
                        </Box>
                        <List dense>
                            {postStats.topContributors && Object.entries(postStats.topContributors)
                                .map(([username, posts], index) => (
                                    <ListItem key={username} sx={{ px: 0 }}>
                                        <Avatar sx={{
                                            width: 32,
                                            height: 32,
                                            mr: 2,
                                            bgcolor: index === 0 ? 'warning.main' : 'grey.400'
                                        }}>
                                            {index === 0 ? '👑' : index + 1}
                                        </Avatar>
                                        <ListItemText
                                            primary={username}
                                            secondary={`${posts} posts`}
                                        />
                                        {index === 0 && (
                                            <Chip
                                                label="Top"
                                                size="small"
                                                color="warning"
                                            />
                                        )}
                                    </ListItem>
                                ))
                            }
                            {(!postStats.topContributors || Object.keys(postStats.topContributors).length === 0) && (
                                <Typography variant="body2" color="textSecondary">
                                    No contributors yet
                                </Typography>
                            )}
                        </List>
                    </Paper>
                </Grid>

                {/* Report Statistics */}
                <Grid item xs={12}>
                    <Paper elevation={2} sx={{ p: 3 }}>
                        <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
                            <ReportIcon sx={{ mr: 1, color: 'error.main' }} />
                            <Typography variant="h6" fontWeight="bold">
                                Report Management
                            </Typography>
                        </Box>
                        <Grid container spacing={2}>
                            <Grid item xs={6} sm={3}>
                                <Box sx={{ textAlign: 'center' }}>
                                    <Typography variant="h4" color="error.main">
                                        {reportStats.pendingReports || 0}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Pending
                                    </Typography>
                                </Box>
                            </Grid>
                            <Grid item xs={6} sm={3}>
                                <Box sx={{ textAlign: 'center' }}>
                                    <Typography variant="h4" color="warning.main">
                                        {reportStats.underReviewReports || 0}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Under Review
                                    </Typography>
                                </Box>
                            </Grid>
                            <Grid item xs={6} sm={3}>
                                <Box sx={{ textAlign: 'center' }}>
                                    <Typography variant="h4" color="success.main">
                                        {reportStats.resolvedReports || 0}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Resolved
                                    </Typography>
                                </Box>
                            </Grid>
                            <Grid item xs={6} sm={3}>
                                <Box sx={{ textAlign: 'center' }}>
                                    <Typography variant="h4" color="grey.500">
                                        {reportStats.dismissedReports || 0}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        Dismissed
                                    </Typography>
                                </Box>
                            </Grid>
                            <Grid item xs={12}>
                                <Box sx={{ mt: 2 }}>
                                    <Typography variant="body2" color="textSecondary" gutterBottom>
                                        Resolution Rate
                                    </Typography>
                                    <LinearProgress
                                        variant="determinate"
                                        value={reportStats.resolutionRate || 0}
                                        sx={{ height: 10, borderRadius: 5 }}
                                        color="success"
                                    />
                                    <Typography variant="caption" color="textSecondary">
                                        {reportStats.resolutionRate || 0}% of reports resolved
                                    </Typography>
                                </Box>
                            </Grid>
                        </Grid>
                    </Paper>
                </Grid>
            </Grid>
        </Container>
    );
}

export default Analytics;
