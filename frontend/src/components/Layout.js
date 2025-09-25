import React, { useState, useEffect } from 'react';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import {
    AppBar,
    Toolbar,
    Typography,
    Button,
    Box,
    Drawer,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    IconButton,
    Avatar,
    Menu,
    MenuItem,
    Divider,
    Chip,
    useTheme,
    alpha
} from '@mui/material';
import {
    Menu as MenuIcon,
    Dashboard as DashboardIcon,
    Article as ArticleIcon,
    Person as PersonIcon,
    Search as SearchIcon,
    Notifications as NotificationsIcon,
    Logout as LogoutIcon,
    Settings as SettingsIcon,
    Report as ReportIcon,
    AdminPanelSettings as AdminIcon,
    Analytics as AnalyticsIcon
} from '@mui/icons-material';
import axios from 'axios';

function Layout() {
    const navigate = useNavigate();
    const location = useLocation();
    const theme = useTheme();
    const [drawerOpen, setDrawerOpen] = useState(false);
    const [anchorEl, setAnchorEl] = useState(null);
    const [userRole, setUserRole] = useState('USER');
    const username = localStorage.getItem('username');

    useEffect(() => {
        // Check user role on component mount
        checkUserRole();
    }, []);

    const checkUserRole = async () => {
        try {
            const response = await axios.get('/huddle/reports/debug/auth');
            const authorities = response.data.authorities || [];
            const isAdmin = authorities.includes('ADMIN') || authorities.includes('ROLE_ADMIN');
            const role = isAdmin ? 'ADMIN' : 'USER';
            setUserRole(role);
            localStorage.setItem('userRole', role);
        } catch (error) {
            console.log('Could not fetch user role:', error);
            // Try to get from localStorage if API fails
            const storedRole = localStorage.getItem('userRole');
            if (storedRole) {
                setUserRole(storedRole);
            }
        }
    };

    const menuItems = [
        { text: 'Home', path: '/', icon: <DashboardIcon /> },
        { text: 'Articles', path: '/articles', icon: <ArticleIcon /> },
        { text: 'Profile', path: '/profile/me', icon: <PersonIcon /> },
        { text: 'Search', path: '/search', icon: <SearchIcon /> },
        { text: 'Notifications', path: '/notifications', icon: <NotificationsIcon /> },
    ];

    // Add admin menu items if user is admin
    const adminMenuItems = userRole === 'ADMIN' ? [
        { text: 'Reports', path: '/reports', icon: <ReportIcon /> },
        { text: 'Analytics', path: '/analytics', icon: <AnalyticsIcon /> },
    ] : [];

    const allMenuItems = [...menuItems, ...adminMenuItems];

    const handleLogout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        localStorage.removeItem('userRole');
        delete axios.defaults.headers.common['Authorization'];
        navigate('/login');
    };

    const handleProfileMenuOpen = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleProfileMenuClose = () => {
        setAnchorEl(null);
    };

    const handleProfileClick = () => {
        navigate('/profile/me');
        handleProfileMenuClose();
    };

    return (
        <Box sx={{ display: 'flex', minHeight: '100vh' }}>
            <AppBar
                position="fixed"
                sx={{
                    zIndex: theme.zIndex.drawer + 1,
                    background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
                    boxShadow: theme.shadows[4]
                }}
            >
                <Toolbar>
                    <IconButton
                        edge="start"
                        color="inherit"
                        aria-label="menu"
                        onClick={() => setDrawerOpen(!drawerOpen)}
                        sx={{ mr: 2 }}
                    >
                        <MenuIcon />
                    </IconButton>

                    <Typography
                        variant="h5"
                        noWrap
                        component="div"
                        sx={{
                            flexGrow: 1,
                            fontWeight: 700,
                            letterSpacing: '-0.5px',
                            cursor: 'pointer'
                        }}
                        onClick={() => navigate('/')}
                    >
                        Huddle
                    </Typography>

                    {userRole === 'ADMIN' && (
                        <Chip
                            icon={<AdminIcon />}
                            label="Admin"
                            size="small"
                            sx={{
                                mr: 2,
                                bgcolor: alpha(theme.palette.common.white, 0.2),
                                color: 'white'
                            }}
                        />
                    )}

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <Typography variant="body2" sx={{ display: { xs: 'none', sm: 'block' } }}>
                            {username}
                        </Typography>

                        <IconButton onClick={handleProfileMenuOpen} sx={{ p: 0 }}>
                            <Avatar sx={{ bgcolor: theme.palette.secondary.main }}>
                                {username?.[0]?.toUpperCase()}
                            </Avatar>
                        </IconButton>

                        <Menu
                            anchorEl={anchorEl}
                            open={Boolean(anchorEl)}
                            onClose={handleProfileMenuClose}
                            transformOrigin={{ horizontal: 'right', vertical: 'top' }}
                            anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
                        >
                            <MenuItem onClick={handleProfileClick}>
                                <ListItemIcon>
                                    <PersonIcon fontSize="small" />
                                </ListItemIcon>
                                Profile
                            </MenuItem>
                            <MenuItem onClick={() => { navigate('/settings'); handleProfileMenuClose(); }}>
                                <ListItemIcon>
                                    <SettingsIcon fontSize="small" />
                                </ListItemIcon>
                                Settings
                            </MenuItem>
                            <Divider />
                            <MenuItem onClick={handleLogout}>
                                <ListItemIcon>
                                    <LogoutIcon fontSize="small" />
                                </ListItemIcon>
                                Logout
                            </MenuItem>
                        </Menu>
                    </Box>
                </Toolbar>
            </AppBar>

            <Drawer
                variant="temporary"
                open={drawerOpen}
                onClose={() => setDrawerOpen(false)}
                sx={{
                    width: 240,
                    flexShrink: 0,
                    '& .MuiDrawer-paper': {
                        width: 240,
                        boxSizing: 'border-box',
                        pt: 8,
                        background: theme.palette.background.default,
                        borderRight: `1px solid ${theme.palette.divider}`
                    },
                }}
            >
                <List>
                    {allMenuItems.map((item) => {
                        const isActive = location.pathname === item.path;
                        return (
                            <ListItem
                                button
                                key={item.text}
                                onClick={() => {
                                    navigate(item.path);
                                    setDrawerOpen(false);
                                }}
                                sx={{
                                    mx: 1,
                                    my: 0.5,
                                    borderRadius: 2,
                                    backgroundColor: isActive ? alpha(theme.palette.primary.main, 0.1) : 'transparent',
                                    color: isActive ? theme.palette.primary.main : 'inherit',
                                    '&:hover': {
                                        backgroundColor: alpha(theme.palette.primary.main, 0.05)
                                    }
                                }}
                            >
                                <ListItemIcon sx={{ color: isActive ? theme.palette.primary.main : 'inherit' }}>
                                    {item.icon}
                                </ListItemIcon>
                                <ListItemText primary={item.text} />
                            </ListItem>
                        );
                    })}

                    <Divider sx={{ my: 2 }} />

                    <ListItem
                        button
                        onClick={handleLogout}
                        sx={{
                            mx: 1,
                            borderRadius: 2,
                            color: theme.palette.error.main,
                            '&:hover': {
                                backgroundColor: alpha(theme.palette.error.main, 0.05)
                            }
                        }}
                    >
                        <ListItemIcon sx={{ color: theme.palette.error.main }}>
                            <LogoutIcon />
                        </ListItemIcon>
                        <ListItemText primary="Logout" />
                    </ListItem>
                </List>
            </Drawer>

            <Box
                component="main"
                sx={{
                    flexGrow: 1,
                    pt: 8,
                    backgroundColor: theme.palette.background.default,
                    minHeight: '100vh'
                }}
            >
                <Outlet />
            </Box>
        </Box>
    );
}

export default Layout;
