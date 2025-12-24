import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme, CssBaseline } from '@mui/material';
import axios from 'axios';

// Import components
import Layout from './components/Layout';
import ErrorBoundary from './components/ErrorBoundary';

// Import pages
import Dashboard from './pages/Dashboard';
import Articles from './pages/Articles';
import Analytics from './pages/Analytics';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Profile from './pages/Profile';
import Notifications from './pages/Notifications';
import Search from './pages/Search';
import ArticleDetail from './pages/ArticleDetail';
import Reports from './pages/Reports';
import ForgotPassword from './pages/ForgotPassword';
import ResetPassword from './pages/ResetPassword';

const theme = createTheme({
    palette: {
        mode: 'light',
        primary: {
            main: '#6366f1',
            light: '#818cf8',
            dark: '#4f46e5',
        },
        secondary: {
            main: '#ec4899',
            light: '#f472b6',
            dark: '#db2777',
        },
        background: {
            default: '#fafafa',
            paper: '#ffffff',
        },
    },
    typography: {
        fontFamily: '"Inter", "SF Pro Display", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
        h1: { fontWeight: 700 },
        h2: { fontWeight: 700 },
        h3: { fontWeight: 700 },
        h4: { fontWeight: 600 },
        h5: { fontWeight: 600 },
        h6: { fontWeight: 600 },
    },
    shape: {
        borderRadius: 12,
    },
    components: {
        MuiButton: {
            styleOverrides: {
                root: {
                    textTransform: 'none',
                    borderRadius: 8,
                    fontWeight: 600,
                },
            },
        },
        MuiPaper: {
            styleOverrides: {
                root: {
                    backgroundImage: 'none',
                },
            },
        },
        MuiCard: {
            styleOverrides: {
                root: {
                    boxShadow: '0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)',
                },
            },
        },
    },
});

// Configure axios defaults
axios.defaults.baseURL = process.env.REACT_APP_API_URL || 'http://localhost:8080';
axios.defaults.withCredentials = true;

// Set up authorization header if token exists on app load
const token = localStorage.getItem('token');
if (token) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

// Add request interceptor to include JWT token
axios.interceptors.request.use(
    (config) => {
        const currentToken = localStorage.getItem('token');
        if (currentToken) {
            config.headers.Authorization = `Bearer ${currentToken}`;
        }
        config.withCredentials = true;
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Add response interceptor to handle auth errors
axios.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('username');
            delete axios.defaults.headers.common['Authorization'];

            if (!['/login', '/signup'].includes(window.location.pathname)) {
                window.location.href = '/login';
            }
        }
        return Promise.reject(error);
    }
);

// Protected Route component
function ProtectedRoute({ children }) {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');

        if (token) {
            axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            setIsAuthenticated(true);
        } else {
            setIsAuthenticated(false);
        }

        setLoading(false);
    }, []);

    if (loading) {
        return null;
    }

    return isAuthenticated ? children : <Navigate to="/login" />;
}

// Component to handle current user profile
function CurrentUserProfile() {
    const [currentUserId, setCurrentUserId] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Fetch current user's ID using the /huddle/me endpoint
        const fetchCurrentUserId = async () => {
            try {
                const response = await axios.get('/huddle/me');
                if (response.data.success && response.data.data) {
                    setCurrentUserId(response.data.data.userId);
                }
            } catch (err) {
                console.error('Failed to fetch current user ID:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchCurrentUserId();
    }, []);

    if (loading) {
        return <div>Loading...</div>;
    }

    // Redirect to the user's actual profile page using their userId
    if (currentUserId) {
        return <Navigate to={`/profile/${currentUserId}`} replace />;
    }

    // If no userId found, redirect to home
    return <Navigate to="/" replace />;
}

function App() {
    useEffect(() => {
        console.log('App mounted - Axios configuration:');
        console.log('Base URL:', axios.defaults.baseURL);
        console.log('With Credentials:', axios.defaults.withCredentials);
        console.log('Auth Header:', axios.defaults.headers.common['Authorization'] ? 'Set' : 'Not set');
    }, []);

    return (
        <ErrorBoundary>
            <ThemeProvider theme={theme}>
                <CssBaseline />
                <Router>
                    <Routes>
                        {/* Public routes */}
                        <Route path="/login" element={<Login />} />
                        <Route path="/signup" element={<Signup />} />
                        <Route path="/forgot-password" element={<ForgotPassword />} />
                        <Route path="/reset-password" element={<ResetPassword />} />

                        {/* Protected routes with Layout */}
                        <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
                            <Route path="/" element={<Dashboard />} />
                            <Route path="/articles" element={<Articles />} />
                            <Route path="/articles/:articleId" element={<ArticleDetail />} />
                            <Route path="/analytics" element={<Analytics />} />

                            {/* Profile routes */}
                            <Route path="/profile" element={<CurrentUserProfile />} />
                            <Route path="/profile/me" element={<CurrentUserProfile />} />
                            {/*<Route path="/profile/:userId" element={<Profile />} />*/}
                            <Route path="/profile/:username" element={<Profile />} />

                            <Route path="/notifications" element={<Notifications />} />
                            <Route path="/search" element={<Search />} />
                            <Route path="/reports" element={<Reports />} />
                        </Route>

                        {/* Default redirect */}
                        <Route path="*" element={<Navigate to="/" />} />
                    </Routes>
                </Router>
            </ThemeProvider>
        </ErrorBoundary>
    );
}

export default App;
