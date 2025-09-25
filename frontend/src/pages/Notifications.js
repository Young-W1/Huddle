import React, { useState, useEffect } from 'react';
import {
    Container,
    Paper,
    Typography,
    List,
    ListItem,
    ListItemText,
    ListItemIcon,
    IconButton,
    Box,
    Badge,
    Button,
    Divider,
    CircularProgress
} from '@mui/material';
import {
    Notifications as NotificationIcon,
    MarkEmailRead,
    Circle
} from '@mui/icons-material';
import axios from 'axios';

function Notifications() {
    const [notifications, setNotifications] = useState([]);
    const [unreadCount, setUnreadCount] = useState(0);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchNotifications();
        fetchUnreadCount();
    }, []);

    const fetchNotifications = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('http://localhost:6061/huddle/notifications', {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (response.data.success) {
                setNotifications(response.data.data.content || []);
            }
            setLoading(false);
        } catch (err) {
            console.error('Failed to fetch notifications:', err);
            setLoading(false);
        }
    };

    const fetchUnreadCount = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('http://localhost:6061/huddle/notifications/unread-count', {
                headers: { Authorization: `Bearer ${token}` }
            });
            if (response.data.success) {
                setUnreadCount(response.data.data);
            }
        } catch (err) {
            console.error('Failed to fetch unread count:', err);
        }
    };

    const markAsRead = async (notificationId) => {
        try {
            const token = localStorage.getItem('token');
            await axios.post(`http://localhost:6061/huddle/notifications/mark-read/${notificationId}`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            fetchNotifications();
            fetchUnreadCount();
        } catch (err) {
            console.error('Failed to mark as read:', err);
        }
    };

    const markAllAsRead = async () => {
        try {
            const token = localStorage.getItem('token');
            await axios.post('http://localhost:6061/huddle/notifications/mark-all-read', {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
            fetchNotifications();
            fetchUnreadCount();
        } catch (err) {
            console.error('Failed to mark all as read:', err);
        }
    };

    if (loading) return <Container><CircularProgress /></Container>;

    return (
        <Container maxWidth="md">
            <Paper sx={{ p: 3 }}>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                    <Box display="flex" alignItems="center">
                        <Badge badgeContent={unreadCount} color="primary">
                            <NotificationIcon fontSize="large" />
                        </Badge>
                        <Typography variant="h5" sx={{ ml: 2 }}>Notifications</Typography>
                    </Box>
                    <Button variant="outlined" onClick={markAllAsRead} startIcon={<MarkEmailRead />}>
                        Mark All Read
                    </Button>
                </Box>
                <Divider />
                <List>
                    {notifications.length > 0 ? (
                        notifications.map((notification) => (
                            <ListItem
                                key={notification.id}
                                secondaryAction={
                                    !notification.isRead && (
                                        <IconButton onClick={() => markAsRead(notification.id)}>
                                            <MarkEmailRead />
                                        </IconButton>
                                    )
                                }
                            >
                                <ListItemIcon>
                                    <Circle color={notification.isRead ? "disabled" : "primary"} fontSize="small" />
                                </ListItemIcon>
                                <ListItemText
                                    primary={notification.message}
                                    secondary={new Date(notification.createdAt).toLocaleString()}
                                />
                            </ListItem>
                        ))
                    ) : (
                        <Typography sx={{ p: 2, textAlign: 'center' }}>No notifications</Typography>
                    )}
                </List>
            </Paper>
        </Container>
    );
}

export default Notifications;
