import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Container,
    Typography,
    Box,
    Button,
    Grid,
    Card,
    CardContent,
    Stack,
    useTheme,
    alpha,
    Paper,
    Avatar,
    Divider
} from '@mui/material';
import {
    Article,
    People,
    TrendingUp,
    Forum,
    RocketLaunch,
    Groups,
    AutoAwesome,
    ArrowForward,
    Create,
    Explore,
    ConnectWithoutContact,
    Lightbulb,
    Speed,
    Security
} from '@mui/icons-material';

function Dashboard() {
    const navigate = useNavigate();
    const theme = useTheme();
    const username = localStorage.getItem('username');

    const features = [
        {
            icon: <Article sx={{ fontSize: 40 }} />,
            title: 'Share Your Knowledge',
            description: 'Write articles, share insights, and contribute to the community knowledge base.',
            color: theme.palette.primary.main
        },
        {
            icon: <Groups sx={{ fontSize: 40 }} />,
            title: 'Build Your Network',
            description: 'Connect with like-minded individuals, follow experts, and grow your professional network.',
            color: theme.palette.secondary.main
        },
        {
            icon: <Forum sx={{ fontSize: 40 }} />,
            title: 'Engage in Discussions',
            description: 'Comment on articles, share perspectives, and engage in meaningful conversations.',
            color: '#9333ea'
        },
        {
            icon: <TrendingUp sx={{ fontSize: 40 }} />,
            title: 'Track Your Growth',
            description: 'Monitor your articles performance, followers growth, and community impact.',
            color: '#10b981'
        }
    ];

    const benefits = [
        { icon: <Speed />, text: 'Lightning Fast Performance' },
        { icon: <Security />, text: 'Secure & Private' },
        { icon: <Lightbulb />, text: 'Innovative Features' },
        { icon: <ConnectWithoutContact />, text: 'Seamless Connectivity' }
    ];

    return (
        <Box sx={{ minHeight: '100vh', bgcolor: 'background.default' }}>
            {/* Hero Section */}
            <Box
                sx={{
                    background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
                    color: 'white',
                    pt: 8,
                    pb: 12,
                    position: 'relative',
                    overflow: 'hidden',
                    '&::before': {
                        content: '""',
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        background: 'url("data:image/svg+xml,%3Csvg width="60" height="60" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"%3E%3Cg fill="none" fill-rule="evenodd"%3E%3Cg fill="%239C92AC" fill-opacity="0.1"%3E%3Cpath d="M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z"/%3E%3C/g%3E%3C/g%3E%3C/svg%3E")',
                        opacity: 0.1
                    }
                }}
            >
                <Container maxWidth="lg">
                    <Grid container spacing={6} alignItems="center">
                        <Grid item xs={12} md={6}>
                            <Box sx={{ animation: 'fadeInLeft 1s ease-out' }}>
                                <Typography
                                    variant="h6"
                                    sx={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        gap: 1,
                                        mb: 2,
                                        fontWeight: 600
                                    }}
                                >
                                    <RocketLaunch /> Welcome back, {username}!
                                </Typography>
                                <Typography
                                    variant="h2"
                                    sx={{
                                        fontWeight: 800,
                                        mb: 3,
                                        fontSize: { xs: '2.5rem', md: '3.5rem' }
                                    }}
                                >
                                    Welcome to Huddle
                                </Typography>
                                <Typography
                                    variant="h5"
                                    sx={{
                                        mb: 4,
                                        opacity: 0.95,
                                        lineHeight: 1.6,
                                        fontWeight: 300
                                    }}
                                >
                                    Your collaborative platform for knowledge sharing, networking, and professional growth.
                                </Typography>
                                <Stack direction="row" spacing={2}>
                                    <Button
                                        variant="contained"
                                        size="large"
                                        startIcon={<Create />}
                                        onClick={() => navigate('/articles')}
                                        sx={{
                                            bgcolor: 'white',
                                            color: theme.palette.primary.main,
                                            px: 4,
                                            py: 1.5,
                                            '&:hover': {
                                                bgcolor: alpha('#fff', 0.9),
                                                transform: 'translateY(-2px)',
                                                boxShadow: theme.shadows[8]
                                            }
                                        }}
                                    >
                                        Write Article
                                    </Button>
                                    <Button
                                        variant="outlined"
                                        size="large"
                                        startIcon={<Explore />}
                                        onClick={() => navigate('/search')}
                                        sx={{
                                            borderColor: 'white',
                                            color: 'white',
                                            px: 4,
                                            py: 1.5,
                                            '&:hover': {
                                                borderColor: 'white',
                                                bgcolor: alpha('#fff', 0.1),
                                                transform: 'translateY(-2px)'
                                            }
                                        }}
                                    >
                                        Explore
                                    </Button>
                                </Stack>
                            </Box>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <Box
                                sx={{
                                    position: 'relative',
                                    animation: 'fadeInRight 1s ease-out',
                                    display: { xs: 'none', md: 'block' }
                                }}
                            >
                                <Box
                                    sx={{
                                        position: 'relative',
                                        width: '100%',
                                        height: 400,
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center'
                                    }}
                                >
                                    <AutoAwesome
                                        sx={{
                                            fontSize: 300,
                                            opacity: 0.2,
                                            position: 'absolute',
                                            animation: 'pulse 3s ease-in-out infinite'
                                        }}
                                    />
                                    <Stack spacing={2} sx={{ position: 'relative', zIndex: 1 }}>
                                        <Paper
                                            elevation={3}
                                            sx={{
                                                p: 2,
                                                bgcolor: alpha('#fff', 0.95),
                                                animation: 'float 3s ease-in-out infinite'
                                            }}
                                        >
                                            <Typography color="primary" fontWeight={600}>
                                                100+ Active Users
                                            </Typography>
                                        </Paper>
                                        <Paper
                                            elevation={3}
                                            sx={{
                                                p: 2,
                                                bgcolor: alpha('#fff', 0.95),
                                                animation: 'float 3s ease-in-out infinite 0.5s'
                                            }}
                                        >
                                            <Typography color="secondary" fontWeight={600}>
                                                500+ Articles Published
                                            </Typography>
                                        </Paper>
                                        <Paper
                                            elevation={3}
                                            sx={{
                                                p: 2,
                                                bgcolor: alpha('#fff', 0.95),
                                                animation: 'float 3s ease-in-out infinite 1s'
                                            }}
                                        >
                                            <Typography sx={{ color: '#9333ea' }} fontWeight={600}>
                                                1000+ Connections Made
                                            </Typography>
                                        </Paper>
                                    </Stack>
                                </Box>
                            </Box>
                        </Grid>
                    </Grid>
                </Container>
            </Box>

            {/* Features Section */}
            <Container maxWidth="lg" sx={{ mt: -6, position: 'relative', zIndex: 1 }}>
                <Grid container spacing={3}>
                    {features.map((feature, index) => (
                        <Grid item xs={12} sm={6} md={3} key={index}>
                            <Card
                                sx={{
                                    height: '100%',
                                    transition: 'all 0.3s',
                                    cursor: 'pointer',
                                    '&:hover': {
                                        transform: 'translateY(-8px)',
                                        boxShadow: theme.shadows[20]
                                    }
                                }}
                            >
                                <CardContent sx={{ textAlign: 'center', p: 3 }}>
                                    <Avatar
                                        sx={{
                                            width: 80,
                                            height: 80,
                                            bgcolor: alpha(feature.color, 0.1),
                                            color: feature.color,
                                            margin: '0 auto',
                                            mb: 2
                                        }}
                                    >
                                        {feature.icon}
                                    </Avatar>
                                    <Typography variant="h6" gutterBottom fontWeight={600}>
                                        {feature.title}
                                    </Typography>
                                    <Typography variant="body2" color="textSecondary">
                                        {feature.description}
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>

                {/* About Section */}
                <Paper
                    elevation={0}
                    sx={{
                        mt: 8,
                        p: 6,
                        background: `linear-gradient(135deg, ${alpha(theme.palette.primary.main, 0.05)} 0%, ${alpha(theme.palette.secondary.main, 0.05)} 100%)`,
                        borderRadius: 3
                    }}
                >
                    <Grid container spacing={4} alignItems="center">
                        <Grid item xs={12} md={6}>
                            <Typography variant="h3" gutterBottom fontWeight={700}>
                                What is Huddle?
                            </Typography>
                            <Typography variant="body1" paragraph color="textSecondary" sx={{ fontSize: '1.1rem', lineHeight: 1.8 }}>
                                Huddle is a modern knowledge-sharing platform designed to foster collaboration and professional growth.
                                Whether you're a developer, designer, writer, or any professional, Huddle provides the perfect space
                                to share your expertise, learn from others, and build meaningful connections.
                            </Typography>
                            <Typography variant="body1" paragraph color="textSecondary" sx={{ fontSize: '1.1rem', lineHeight: 1.8 }}>
                                Our platform combines the best features of blogging, social networking, and knowledge management
                                to create a unique ecosystem where ideas flourish and communities thrive.
                            </Typography>
                            <Button
                                variant="contained"
                                endIcon={<ArrowForward />}
                                onClick={() => navigate('/articles')}
                                sx={{ mt: 2 }}
                            >
                                Get Started
                            </Button>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <Box sx={{ pl: { md: 4 } }}>
                                <Typography variant="h5" gutterBottom fontWeight={600}>
                                    Why Choose Huddle?
                                </Typography>
                                <Stack spacing={2} sx={{ mt: 3 }}>
                                    {benefits.map((benefit, index) => (
                                        <Box key={index} display="flex" alignItems="center" gap={2}>
                                            <Avatar
                                                sx={{
                                                    width: 40,
                                                    height: 40,
                                                    bgcolor: alpha(theme.palette.primary.main, 0.1),
                                                    color: theme.palette.primary.main
                                                }}
                                            >
                                                {benefit.icon}
                                            </Avatar>
                                            <Typography variant="body1">
                                                {benefit.text}
                                            </Typography>
                                        </Box>
                                    ))}
                                </Stack>
                            </Box>
                        </Grid>
                    </Grid>
                </Paper>

                {/* Call to Action */}
                <Box
                    sx={{
                        mt: 8,
                        mb: 4,
                        p: 4,
                        textAlign: 'center',
                        borderRadius: 3,
                        background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
                        color: 'white'
                    }}
                >
                    <Typography variant="h4" gutterBottom fontWeight={700}>
                        Ready to Make an Impact?
                    </Typography>
                    <Typography variant="h6" sx={{ mb: 3, opacity: 0.95 }}>
                        Join thousands of professionals sharing knowledge and building connections.
                    </Typography>
                    <Stack direction="row" spacing={2} justifyContent="center">
                        <Button
                            variant="contained"
                            size="large"
                            startIcon={<Article />}
                            onClick={() => navigate('/articles')}
                            sx={{
                                bgcolor: 'white',
                                color: theme.palette.primary.main,
                                '&:hover': {
                                    bgcolor: alpha('#fff', 0.9)
                                }
                            }}
                        >
                            Browse Articles
                        </Button>
                        <Button
                            variant="outlined"
                            size="large"
                            startIcon={<People />}
                            onClick={() => navigate('/search')}
                            sx={{
                                borderColor: 'white',
                                color: 'white',
                                '&:hover': {
                                    borderColor: 'white',
                                    bgcolor: alpha('#fff', 0.1)
                                }
                            }}
                        >
                            Find People
                        </Button>
                    </Stack>
                </Box>
            </Container>

            {/* Add CSS animations */}
            <style>
                {`
                    @keyframes fadeInLeft {
                        from {
                            opacity: 0;
                            transform: translateX(-50px);
                        }
                        to {
                            opacity: 1;
                            transform: translateX(0);
                        }
                    }
                    
                    @keyframes fadeInRight {
                        from {
                            opacity: 0;
                            transform: translateX(50px);
                        }
                        to {
                            opacity: 1;
                            transform: translateX(0);
                        }
                    }
                    
                    @keyframes float {
                        0%, 100% {
                            transform: translateY(0px);
                        }
                        50% {
                            transform: translateY(-20px);
                        }
                    }
                    
                    @keyframes pulse {
                        0%, 100% {
                            transform: scale(1);
                            opacity: 0.2;
                        }
                        50% {
                            transform: scale(1.1);
                            opacity: 0.3;
                        }
                    }
                `}
            </style>
        </Box>
    );
}

export default Dashboard;
