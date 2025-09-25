import React, { useState, useEffect } from 'react';
import {
    Container,
    Typography,
    Paper,
    Grid,
    Box,
    CircularProgress,
    Alert
} from '@mui/material';
import axios from 'axios';

function Analytics() {
    const [analytics, setAnalytics] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchAnalytics();
    }, []);

    const fetchAnalytics = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('http://localhost:6061/huddle/analytics/all', {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (response.data.success) {
                setAnalytics(response.data.data);
            }
            setLoading(false);
        } catch (err) {
            setError('You need admin privileges to view analytics');
            setLoading(false);
        }
    };

    if (loading) return <Container><CircularProgress /></Container>;
    if (error) return <Container><Alert severity="error">{error}</Alert></Container>;

    return (
        <Container maxWidth="lg">
            <Typography variant="h4" gutterBottom>Analytics</Typography>

            {analytics ? (
                <Grid container spacing={3}>
                    <Grid item xs={12} md={4}>
                        <Paper sx={{ p: 3 }}>
                            <Typography variant="h6" gutterBottom>User Statistics</Typography>
                            <Typography>Total Users: {analytics.userStats?.totalUsers || 0}</Typography>
                            <Typography>Active Users: {analytics.userStats?.activeUsers || 0}</Typography>
                        </Paper>
                    </Grid>

                    <Grid item xs={12} md={4}>
                        <Paper sx={{ p: 3 }}>
                            <Typography variant="h6" gutterBottom>Post Statistics</Typography>
                            <Typography>Total Posts: {analytics.postStats?.totalPosts || 0}</Typography>
                            <Typography>Published: {analytics.postStats?.publishedPosts || 0}</Typography>
                        </Paper>
                    </Grid>

                    <Grid item xs={12} md={4}>
                        <Paper sx={{ p: 3 }}>
                            <Typography variant="h6" gutterBottom>Report Statistics</Typography>
                            <Typography>Total Reports: {analytics.reportStats?.totalReports || 0}</Typography>
                            <Typography>Pending: {analytics.reportStats?.pendingReports || 0}</Typography>
                        </Paper>
                    </Grid>
                </Grid>
            ) : (
                <Paper sx={{ p: 3 }}>
                    <Typography>Analytics data will appear here for admin users</Typography>
                </Paper>
            )}
        </Container>
    );
}

export default Analytics;
