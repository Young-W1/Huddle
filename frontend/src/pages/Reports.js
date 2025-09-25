import React, { useState, useEffect } from 'react';
import {
    Container,
    Paper,
    Typography,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TablePagination,
    Button,
    Chip,
    Box,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    Alert,
    IconButton,
    Stack,
    Card,
    CardContent,
    Grid,
    Skeleton,
    useTheme,
    alpha
} from '@mui/material';
import {
    Report as ReportIcon,
    CheckCircle,
    Cancel,
    Pending,
    Visibility,
    Edit,
    Article,
    Person,
    CalendarToday,
    FilterList
} from '@mui/icons-material';
import axios from 'axios';

function Reports() {
    const theme = useTheme();
    const [reports, setReports] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalElements, setTotalElements] = useState(0);
    const [selectedReport, setSelectedReport] = useState(null);
    const [statusFilter, setStatusFilter] = useState('');
    const [openDialog, setOpenDialog] = useState(false);
    const [adminNotes, setAdminNotes] = useState('');
    const [newStatus, setNewStatus] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        fetchReports();
    }, [page, rowsPerPage, statusFilter]);

    const fetchReports = async () => {
        setLoading(true);
        try {
            const params = {
                page,
                size: rowsPerPage,
                sort: 'createdAt,desc'
            };

            if (statusFilter) {
                params.status = statusFilter;
            }

            const response = await axios.get('/huddle/reports/all', { params });

            if (response.data.success) {
                setReports(response.data.data.content || []);
                setTotalElements(response.data.data.totalElements || 0);
            }
        } catch (err) {
            console.error('Failed to fetch reports:', err);
            setError('Failed to fetch reports');
        } finally {
            setLoading(false);
        }
    };

    // In Reports.js, update the handleUpdateReport function:
    const handleUpdateReport = async () => {
        try {
            // Ensure status is uppercase and valid
            const validStatus = newStatus.toUpperCase();
            if (!['PENDING', 'RESOLVED', 'DISMISSED'].includes(validStatus)) {
                setError('Invalid status selected');
                return;
            }

            const response = await axios.put(`/huddle/reports/update/${selectedReport.id}`, {
                status: validStatus,
                adminNotes: adminNotes || ''
            });

            if (response.data.success) {
                setSuccess('Report updated successfully');
                setOpenDialog(false);
                fetchReports();
                setSelectedReport(null);
                setNewStatus('');
                setAdminNotes('');
            }
        } catch (err) {
            console.error('Failed to update report:', err);
            setError(err.response?.data?.message || 'Failed to update report');
        }
    };


    const getStatusColor = (status) => {
        switch (status) {
            case 'PENDING': return 'warning';
            case 'RESOLVED': return 'success';
            case 'DISMISSED': return 'default';
            default: return 'default';
        }
    };

    const getStatusIcon = (status) => {
        switch (status) {
            case 'PENDING': return <Pending />;
            case 'RESOLVED': return <CheckCircle />;
            case 'DISMISSED': return <Cancel />;
            default: return <ReportIcon />;
        }
    };

    const handleOpenDialog = (report) => {
        setSelectedReport(report);
        setNewStatus(report.status);
        setAdminNotes(report.adminNotes || '');
        setOpenDialog(true);
    };

    if (loading && reports.length === 0) {
        return (
            <Container maxWidth="lg" sx={{ mt: 4 }}>
                <Stack spacing={2}>
                    <Skeleton variant="rectangular" height={100} />
                    <Skeleton variant="rectangular" height={400} />
                </Stack>
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4 }}>
            {/* Header */}
            <Paper
                elevation={0}
                sx={{
                    p: 3,
                    mb: 3,
                    background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.secondary.main} 100%)`,
                    color: 'white',
                    borderRadius: 2
                }}
            >
                <Box display="flex" alignItems="center" justifyContent="space-between">
                    <Box display="flex" alignItems="center" gap={2}>
                        <ReportIcon sx={{ fontSize: 40 }} />
                        <Box>
                            <Typography variant="h4" fontWeight={700}>
                                Report Management
                            </Typography>
                            <Typography variant="body2" sx={{ opacity: 0.9 }}>
                                Review and manage reported content
                            </Typography>
                        </Box>
                    </Box>
                    <FormControl size="small" sx={{ minWidth: 150, bgcolor: 'white', borderRadius: 1 }}>
                        <InputLabel>Status Filter</InputLabel>
                        <Select
                            value={statusFilter}
                            onChange={(e) => {
                                setStatusFilter(e.target.value);
                                setPage(0);
                            }}
                            label="Status Filter"
                        >
                            <MenuItem value="">All</MenuItem>
                            <MenuItem value="PENDING">Pending</MenuItem>
                            <MenuItem value="RESOLVED">Resolved</MenuItem>
                            <MenuItem value="DISMISSED">Dismissed</MenuItem>
                        </Select>
                    </FormControl>
                </Box>
            </Paper>

            {/* Stats Cards */}
            <Grid container spacing={3} sx={{ mb: 3 }}>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center" justifyContent="space-between">
                                <Box>
                                    <Typography color="textSecondary" gutterBottom>
                                        Pending Reports
                                    </Typography>
                                    <Typography variant="h4">
                                        {reports.filter(r => r.status === 'PENDING').length}
                                    </Typography>
                                </Box>
                                <Pending sx={{ fontSize: 40, color: theme.palette.warning.main }} />
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center" justifyContent="space-between">
                                <Box>
                                    <Typography color="textSecondary" gutterBottom>
                                        Resolved
                                    </Typography>
                                    <Typography variant="h4">
                                        {reports.filter(r => r.status === 'RESOLVED').length}
                                    </Typography>
                                </Box>
                                <CheckCircle sx={{ fontSize: 40, color: theme.palette.success.main }} />
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Box display="flex" alignItems="center" justifyContent="space-between">
                                <Box>
                                    <Typography color="textSecondary" gutterBottom>
                                        Total Reports
                                    </Typography>
                                    <Typography variant="h4">{totalElements}</Typography>
                                </Box>
                                <ReportIcon sx={{ fontSize: 40, color: theme.palette.primary.main }} />
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            {error && <Alert severity="error" onClose={() => setError('')} sx={{ mb: 2 }}>{error}</Alert>}
            {success && <Alert severity="success" onClose={() => setSuccess('')} sx={{ mb: 2 }}>{success}</Alert>}

            {/* Reports Table */}
            <Paper sx={{ width: '100%', overflow: 'hidden' }}>
                <TableContainer sx={{ maxHeight: 600 }}>
                    <Table stickyHeader>
                        <TableHead>
                            <TableRow>
                                <TableCell>Status</TableCell>
                                <TableCell>Article</TableCell>
                                <TableCell>Reported By</TableCell>
                                <TableCell>Reason</TableCell>
                                <TableCell>Description</TableCell>
                                <TableCell>Date</TableCell>
                                <TableCell align="center">Actions</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {reports.map((report) => (
                                <TableRow key={report.id} hover>
                                    <TableCell>
                                        <Chip
                                            icon={getStatusIcon(report.status)}
                                            label={report.status}
                                            color={getStatusColor(report.status)}
                                            size="small"
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <Box display="flex" alignItems="center" gap={1}>
                                            <Article fontSize="small" color="action" />
                                            <Typography variant="body2" noWrap sx={{ maxWidth: 200 }}>
                                                {report.articleTitle}
                                            </Typography>
                                        </Box>
                                    </TableCell>
                                    <TableCell>
                                        <Box display="flex" alignItems="center" gap={1}>
                                            <Person fontSize="small" color="action" />
                                            <Typography variant="body2">
                                                {report.reporterUsername}
                                            </Typography>
                                        </Box>
                                    </TableCell>
                                    <TableCell>
                                        <Chip label={report.reason} size="small" variant="outlined" />
                                    </TableCell>
                                    <TableCell>
                                        <Typography variant="body2" noWrap sx={{ maxWidth: 200 }}>
                                            {report.description}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <Box display="flex" alignItems="center" gap={1}>
                                            <CalendarToday fontSize="small" color="action" />
                                            <Typography variant="caption">
                                                {new Date(report.createdAt).toLocaleDateString()}
                                            </Typography>
                                        </Box>
                                    </TableCell>
                                    <TableCell align="center">
                                        <IconButton
                                            size="small"
                                            onClick={() => handleOpenDialog(report)}
                                            color="primary"
                                        >
                                            <Edit />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
                <TablePagination
                    rowsPerPageOptions={[5, 10, 25]}
                    component="div"
                    count={totalElements}
                    rowsPerPage={rowsPerPage}
                    page={page}
                    onPageChange={(e, newPage) => setPage(newPage)}
                    onRowsPerPageChange={(e) => {
                        setRowsPerPage(parseInt(e.target.value, 10));
                        setPage(0);
                    }}
                />
            </Paper>

            {/* Update Dialog */}
            <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
                <DialogTitle>
                    Update Report
                </DialogTitle>
                <DialogContent>
                    <Stack spacing={3} sx={{ mt: 2 }}>
                        {selectedReport && (
                            <>
                                <Box>
                                    <Typography variant="subtitle2" color="textSecondary">
                                        Article
                                    </Typography>
                                    <Typography>{selectedReport.articleTitle}</Typography>
                                </Box>
                                <Box>
                                    <Typography variant="subtitle2" color="textSecondary">
                                        Reporter
                                    </Typography>
                                    <Typography>{selectedReport.reporterUsername}</Typography>
                                </Box>
                                <Box>
                                    <Typography variant="subtitle2" color="textSecondary">
                                        Reason
                                    </Typography>
                                    <Typography>{selectedReport.reason}</Typography>
                                </Box>
                                <Box>
                                    <Typography variant="subtitle2" color="textSecondary">
                                        Description
                                    </Typography>
                                    <Typography>{selectedReport.description}</Typography>
                                </Box>
                                <FormControl fullWidth>
                                    <InputLabel>Status</InputLabel>
                                    <Select
                                        value={newStatus}
                                        onChange={(e) => setNewStatus(e.target.value)}
                                        label="Status"
                                    >
                                        <MenuItem value="PENDING">Pending</MenuItem>
                                        <MenuItem value="RESOLVED">Resolved</MenuItem>
                                        <MenuItem value="DISMISSED">Dismissed</MenuItem>
                                    </Select>
                                </FormControl>
                                <TextField
                                    fullWidth
                                    label="Admin Notes"
                                    multiline
                                    rows={4}
                                    value={adminNotes}
                                    onChange={(e) => setAdminNotes(e.target.value)}
                                    placeholder="Add notes about the resolution..."
                                />
                            </>
                        )}
                    </Stack>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDialog(false)}>Cancel</Button>
                    <Button onClick={handleUpdateReport} variant="contained">
                        Update Report
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
}

export default Reports;
