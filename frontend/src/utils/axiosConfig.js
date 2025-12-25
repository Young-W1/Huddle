import axios from 'axios';

// API Configuration
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:6061';

// Configure axios defaults
axios.defaults.baseURL = API_BASE_URL;
axios.defaults.timeout = 30000;
axios.defaults.headers.common['Content-Type'] = 'application/json';

// Request interceptor - adds JWT token to all requests
axios.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor - handles common errors
axios.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            const { status, data } = error.response;

            // Handle 401 Unauthorized - clear auth and redirect to login
            if (status === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                if (window.location.pathname !== '/login') {
                    window.location.href = '/login';
                }
            }

            // Handle 429 Rate Limiting
            if (status === 429) {
                console.error('Rate limit exceeded:', data?.message || 'Please try again later');
            }

            // Handle 500 Server Errors
            if (status >= 500) {
                console.error('Server error:', data?.message || 'An unexpected error occurred');
            }
        } else if (error.request) {
            // Network error - no response received
            console.error('Network error: Unable to reach the server');
        }

        return Promise.reject(error);
    }
);

export default axios;
