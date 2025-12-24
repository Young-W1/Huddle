import axios from 'axios';

// API Configuration
const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:6061';
const DEFAULT_PAGE_SIZE = parseInt(process.env.REACT_APP_DEFAULT_PAGE_SIZE || '10');

// Create axios instance with default configuration
const api = axios.create({
    baseURL: `${API_BASE_URL}/huddle`,
    headers: {
        'Content-Type': 'application/json',
    },
    timeout: 30000, // 30 second timeout
});

// Request interceptor - adds JWT token to all requests
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

// Response interceptor - handles common errors
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            const { status } = error.response;

            // Handle 401 Unauthorized - redirect to login
            if (status === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                // Only redirect if not already on login page
                if (window.location.pathname !== '/login') {
                    window.location.href = '/login';
                }
            }

            // Handle 403 Forbidden
            if (status === 403) {
                console.error('Access forbidden - insufficient permissions');
            }

            // Handle rate limiting
            if (status === 429) {
                console.error('Rate limit exceeded - please wait before making more requests');
            }
        }
        return Promise.reject(error);
    }
);

// ===== Authentication Services =====
export const authService = {
    login: (username, password) => api.post('/login', { username, password }),
    signup: (userData) => api.post('/signup', userData),
    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        return api.post('/logout');
    },
};

// ===== Article Services =====
export const articleService = {
    getAllArticles: (params = {}) => {
        const { page = 0, size = DEFAULT_PAGE_SIZE, sort = 'createdAt,desc', ...rest } = params;
        return api.get('/articles/allArticles', { params: { page, size, sort, ...rest } });
    },
    getArticle: (id) => api.get(`/articles/article/${id}`),
    createArticle: (data) => api.post('/articles/create', data),
    updateArticle: (id, data) => api.put(`/articles/update/${id}`, data),
    deleteArticle: (id) => api.delete(`/articles/delete/${id}`),
    searchArticles: (query, params = {}) => {
        const { page = 0, size = DEFAULT_PAGE_SIZE, ...rest } = params;
        return api.get('/articles/search', { params: { query, page, size, ...rest } });
    },
    getMyArticles: (params = {}) => {
        const { page = 0, size = DEFAULT_PAGE_SIZE, sort = 'createdAt,desc' } = params;
        return api.get('/articles/my-articles', { params: { page, size, sort } });
    },
    rateArticle: (articleId, rating) => api.post(`/articles/${articleId}/rate`, { rating }),
};

// ===== Comment Services =====
export const commentService = {
    getComments: (articleId, params = {}) => {
        const { page = 0, size = DEFAULT_PAGE_SIZE } = params;
        return api.get(`/articles/${articleId}/comments`, { params: { page, size } });
    },
    createComment: (articleId, data) => api.post(`/articles/${articleId}/comments`, data),
    updateComment: (articleId, commentId, data) => api.put(`/articles/${articleId}/comments/${commentId}`, data),
    deleteComment: (articleId, commentId) => api.delete(`/articles/${articleId}/comments/${commentId}`),
    voteComment: (articleId, commentId, voteType) =>
        api.post(`/articles/${articleId}/comments/${commentId}/vote`, { voteType }),
};

// ===== User Services =====
export const userService = {
    getProfile: (userId) => api.get(`/users/${userId}/profile`),
    updateProfile: (data) => api.put('/users/profile', data),
    uploadProfilePicture: (file) => {
        const formData = new FormData();
        formData.append('file', file);
        return api.post('/users/profile/picture', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
        });
    },
    followUser: (userId) => api.post(`/users/${userId}/follow`),
    unfollowUser: (userId) => api.delete(`/users/${userId}/unfollow`),
    getFollowers: (userId, params = {}) => {
        const { page = 0, size = DEFAULT_PAGE_SIZE } = params;
        return api.get(`/users/${userId}/followers`, { params: { page, size } });
    },
    getFollowing: (userId, params = {}) => {
        const { page = 0, size = DEFAULT_PAGE_SIZE } = params;
        return api.get(`/users/${userId}/following`, { params: { page, size } });
    },
};

// ===== Report Services =====
export const reportService = {
    createReport: (data) => api.post('/reports', data),
    getReports: (params = {}) => {
        const { page = 0, size = DEFAULT_PAGE_SIZE, status } = params;
        return api.get('/reports', { params: { page, size, status } });
    },
    updateReport: (reportId, data) => api.put(`/reports/${reportId}`, data),
};

// ===== Analytics Services =====
export const analyticsService = {
    getAllAnalytics: () => api.get('/analytics/all'),
    getUserStats: () => api.get('/analytics/users'),
    getPostStats: () => api.get('/analytics/posts'),
    getReportStats: () => api.get('/analytics/reports'),
};

// ===== Notification Services =====
export const notificationService = {
    getNotifications: (params = {}) => {
        const { page = 0, size = DEFAULT_PAGE_SIZE } = params;
        return api.get('/notifications', { params: { page, size } });
    },
    markAsRead: (notificationId) => api.put(`/notifications/${notificationId}/read`),
    markAllAsRead: () => api.put('/notifications/read-all'),
    getUnreadCount: () => api.get('/notifications/unread-count'),
};

// ===== Search Services =====
export const searchService = {
    globalSearch: (query, params = {}) => {
        const { page = 0, size = DEFAULT_PAGE_SIZE } = params;
        return api.get('/search/global', { params: { query, page, size } });
    },
    searchUsers: (params = {}) => {
        const { searchTerm, page = 0, size = DEFAULT_PAGE_SIZE } = params;
        return api.get('/search-users', { params: { searchTerm, page, size } });
    },
};

export default api;
