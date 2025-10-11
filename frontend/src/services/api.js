import axios from 'axios';

// const API_BASE_URL = '/huddle';
// src/config/api.js or wherever you configure axios
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:6061';

const api = axios.create({
    baseURL: `${API_BASE_URL}/huddle`,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add JWT token to requests
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// API methods
export const authService = {
    login: (username, password) => api.post('/login', { username, password }),
    signup: (userData) => api.post('/signup', userData),
    logout: () => api.post('/logout'),
};

export const articleService = {
    getAllArticles: (params) => api.get('/articles/allArticles', { params }),
    getArticle: (id) => api.get(`/articles/article/${id}`),
    createArticle: (data) => api.post('/articles/create', data),
    updateArticle: (id, data) => api.put(`/articles/update/${id}`, data),
    deleteArticle: (id) => api.delete(`/articles/delete/${id}`),
};

export const analyticsService = {
    getAllAnalytics: () => api.get('/analytics/all'),
    getUserStats: () => api.get('/analytics/users'),
    getPostStats: () => api.get('/analytics/posts'),
    getReportStats: () => api.get('/analytics/reports'),
};

export default api;
