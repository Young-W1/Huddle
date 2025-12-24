import React, { useState } from 'react';
import { Link as RouterLink } from 'react-router-dom';
import {
    Container,
    Box,
    Typography,
    TextField,
    Button,
    Paper,
    Alert,
    Link,
    CircularProgress
} from '@mui/material';
import { Email, ArrowBack } from '@mui/icons-material';
import axios from 'axios';

function ForgotPassword() {
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setSuccess('');

        try {
            const response = await axios.post('/huddle/password/forgot', { email });
            if (response.data.success) {
                setSuccess(response.data.message);
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to process request. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container maxWidth="sm">
            <Box sx={{ mt: 8, mb: 4 }}>
                <Paper elevation={3} sx={{ p: 4, borderRadius: 3 }}>
                    <Box sx={{ textAlign: 'center', mb: 4 }}>
                        <Email sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
                        <Typography variant="h4" fontWeight={700} gutterBottom>
                            Forgot Password?
                        </Typography>
                        <Typography variant="body1" color="textSecondary">
                            No worries! Enter your email address and we'll send you a link to reset your password.
                        </Typography>
                    </Box>

                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError('')}>
                            {error}
                        </Alert>
                    )}

                    {success && (
                        <Alert severity="success" sx={{ mb: 3 }}>
                            {success}
                        </Alert>
                    )}

                    <form onSubmit={handleSubmit}>
                        <TextField
                            fullWidth
                            label="Email Address"
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            sx={{ mb: 3 }}
                            placeholder="Enter your registered email"
                        />

                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            size="large"
                            disabled={loading || !email}
                            sx={{ mb: 3, py: 1.5 }}
                        >
                            {loading ? <CircularProgress size={24} /> : 'Send Reset Link'}
                        </Button>
                    </form>

                    <Box sx={{ textAlign: 'center' }}>
                        <Link
                            component={RouterLink}
                            to="/login"
                            sx={{ display: 'inline-flex', alignItems: 'center', textDecoration: 'none' }}
                        >
                            <ArrowBack sx={{ mr: 0.5, fontSize: 18 }} />
                            Back to Login
                        </Link>
                    </Box>
                </Paper>
            </Box>
        </Container>
    );
}

export default ForgotPassword;

