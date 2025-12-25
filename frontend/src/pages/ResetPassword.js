import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate, Link as RouterLink } from 'react-router-dom';
import {
    Container,
    Box,
    Typography,
    TextField,
    Button,
    Paper,
    Alert,
    CircularProgress,
    InputAdornment,
    IconButton,
    Link
} from '@mui/material';
import { LockReset, Visibility, VisibilityOff, CheckCircle, ArrowBack } from '@mui/icons-material';
import axios from 'axios';

function ResetPassword() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const token = searchParams.get('token');

    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [validating, setValidating] = useState(true);
    const [tokenValid, setTokenValid] = useState(false);
    const [success, setSuccess] = useState(false);
    const [error, setError] = useState('');

    useEffect(() => {
        const validateToken = async () => {
            if (!token) {
                setError('No reset token provided. Please request a new password reset link.');
                setValidating(false);
                return;
            }

            try {
                const response = await axios.get(`/huddle/password/validate-token?token=${token}`);
                if (response.data.success && response.data.data?.valid) {
                    setTokenValid(true);
                } else {
                    setError('This password reset link is invalid or has expired. Please request a new one.');
                }
            } catch (err) {
                setError('This password reset link is invalid or has expired. Please request a new one.');
            } finally {
                setValidating(false);
            }
        };

        validateToken();
    }, [token]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (newPassword.length < 8) {
            setError('Password must be at least 8 characters long.');
            return;
        }

        if (newPassword !== confirmPassword) {
            setError('Passwords do not match.');
            return;
        }

        setLoading(true);

        try {
            const response = await axios.post('/huddle/password/reset', {
                token,
                newPassword,
                confirmPassword
            });

            if (response.data.success) {
                setSuccess(true);
            } else {
                setError(response.data.message || 'Failed to reset password.');
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to reset password. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    if (validating) {
        return (
            <Container maxWidth="sm">
                <Box sx={{ mt: 8, display: 'flex', justifyContent: 'center', alignItems: 'center', flexDirection: 'column' }}>
                    <CircularProgress size={60} />
                    <Typography variant="h6" sx={{ mt: 2 }}>
                        Validating reset link...
                    </Typography>
                </Box>
            </Container>
        );
    }

    if (success) {
        return (
            <Container maxWidth="sm">
                <Box sx={{ mt: 8, mb: 4 }}>
                    <Paper elevation={3} sx={{ p: 4, borderRadius: 3, textAlign: 'center' }}>
                        <CheckCircle sx={{ fontSize: 80, color: 'success.main', mb: 2 }} />
                        <Typography variant="h4" fontWeight={700} gutterBottom>
                            Password Reset Successful!
                        </Typography>
                        <Typography variant="body1" color="textSecondary" sx={{ mb: 3 }}>
                            Your password has been successfully reset. You can now log in with your new password.
                        </Typography>
                        <Button
                            variant="contained"
                            size="large"
                            onClick={() => navigate('/login')}
                            sx={{ py: 1.5, px: 4 }}
                        >
                            Go to Login
                        </Button>
                    </Paper>
                </Box>
            </Container>
        );
    }

    if (!tokenValid) {
        return (
            <Container maxWidth="sm">
                <Box sx={{ mt: 8, mb: 4 }}>
                    <Paper elevation={3} sx={{ p: 4, borderRadius: 3, textAlign: 'center' }}>
                        <LockReset sx={{ fontSize: 60, color: 'error.main', mb: 2 }} />
                        <Typography variant="h5" fontWeight={700} gutterBottom>
                            Invalid or Expired Link
                        </Typography>
                        <Alert severity="error" sx={{ mb: 3, textAlign: 'left' }}>
                            {error}
                        </Alert>
                        <Button
                            variant="contained"
                            onClick={() => navigate('/forgot-password')}
                            sx={{ mr: 2 }}
                        >
                            Request New Link
                        </Button>
                        <Button
                            variant="outlined"
                            component={RouterLink}
                            to="/login"
                        >
                            Back to Login
                        </Button>
                    </Paper>
                </Box>
            </Container>
        );
    }

    return (
        <Container maxWidth="sm">
            <Box sx={{ mt: 8, mb: 4 }}>
                <Paper elevation={3} sx={{ p: 4, borderRadius: 3 }}>
                    <Box sx={{ textAlign: 'center', mb: 4 }}>
                        <LockReset sx={{ fontSize: 60, color: 'primary.main', mb: 2 }} />
                        <Typography variant="h4" fontWeight={700} gutterBottom>
                            Reset Your Password
                        </Typography>
                        <Typography variant="body1" color="textSecondary">
                            Please enter your new password below.
                        </Typography>
                    </Box>

                    {error && (
                        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError('')}>
                            {error}
                        </Alert>
                    )}

                    <form onSubmit={handleSubmit}>
                        <TextField
                            fullWidth
                            label="New Password"
                            type={showNewPassword ? 'text' : 'password'}
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            required
                            sx={{ mb: 3 }}
                            placeholder="Enter your new password"
                            InputProps={{
                                endAdornment: (
                                    <InputAdornment position="end">
                                        <IconButton
                                            onClick={() => setShowNewPassword(!showNewPassword)}
                                            edge="end"
                                        >
                                            {showNewPassword ? <VisibilityOff /> : <Visibility />}
                                        </IconButton>
                                    </InputAdornment>
                                )
                            }}
                        />

                        <TextField
                            fullWidth
                            label="Confirm New Password"
                            type={showConfirmPassword ? 'text' : 'password'}
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            required
                            sx={{ mb: 3 }}
                            placeholder="Confirm your new password"
                            InputProps={{
                                endAdornment: (
                                    <InputAdornment position="end">
                                        <IconButton
                                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                            edge="end"
                                        >
                                            {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                                        </IconButton>
                                    </InputAdornment>
                                )
                            }}
                        />

                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            size="large"
                            disabled={loading || !newPassword || !confirmPassword}
                            sx={{ mb: 3, py: 1.5 }}
                        >
                            {loading ? <CircularProgress size={24} /> : 'Reset Password'}
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

export default ResetPassword;

