import React, { Component } from 'react';
import { Box, Typography, Button, Paper } from '@mui/material';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

/**
 * Error Boundary component to catch and display React errors gracefully.
 * Prevents the entire app from crashing on errors.
 */
class ErrorBoundary extends Component {
    constructor(props) {
        super(props);
        this.state = { hasError: false, error: null, errorInfo: null };
    }

    static getDerivedStateFromError(error) {
        return { hasError: true };
    }

    componentDidCatch(error, errorInfo) {
        this.setState({
            error: error,
            errorInfo: errorInfo,
        });

        // Log error to console (in production, send to error tracking service)
        console.error('Error caught by ErrorBoundary:', error, errorInfo);
    }

    handleRetry = () => {
        this.setState({ hasError: false, error: null, errorInfo: null });
        window.location.reload();
    };

    handleGoHome = () => {
        this.setState({ hasError: false, error: null, errorInfo: null });
        window.location.href = '/';
    };

    render() {
        if (this.state.hasError) {
            return (
                <Box
                    sx={{
                        minHeight: '100vh',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        backgroundColor: '#f5f5f5',
                        p: 3,
                    }}
                >
                    <Paper
                        elevation={3}
                        sx={{
                            p: 4,
                            maxWidth: 500,
                            textAlign: 'center',
                            borderRadius: 2,
                        }}
                    >
                        <ErrorOutlineIcon
                            sx={{
                                fontSize: 60,
                                color: 'error.main',
                                mb: 2,
                            }}
                        />
                        <Typography variant="h5" gutterBottom color="error">
                            Something went wrong
                        </Typography>
                        <Typography variant="body1" color="text.secondary" paragraph>
                            We're sorry, but something unexpected happened. Please try again or return to the home page.
                        </Typography>

                        {process.env.NODE_ENV === 'development' && this.state.error && (
                            <Box
                                sx={{
                                    mt: 2,
                                    p: 2,
                                    backgroundColor: '#f5f5f5',
                                    borderRadius: 1,
                                    textAlign: 'left',
                                    overflow: 'auto',
                                    maxHeight: 200,
                                }}
                            >
                                <Typography variant="caption" color="error" component="pre">
                                    {this.state.error.toString()}
                                </Typography>
                            </Box>
                        )}

                        <Box sx={{ mt: 3, display: 'flex', gap: 2, justifyContent: 'center' }}>
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={this.handleRetry}
                            >
                                Try Again
                            </Button>
                            <Button
                                variant="outlined"
                                color="primary"
                                onClick={this.handleGoHome}
                            >
                                Go Home
                            </Button>
                        </Box>
                    </Paper>
                </Box>
            );
        }

        return this.props.children;
    }
}

export default ErrorBoundary;

