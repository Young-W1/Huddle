import React from 'react';
import { Box, Pagination as MuiPagination, Typography, FormControl, Select, MenuItem } from '@mui/material';

/**
 * Reusable Pagination component that displays pagination controls
 * and page size selection.
 */
const Pagination = ({
    page,
    totalPages,
    totalElements,
    size,
    onPageChange,
    onSizeChange,
    showPageSize = true,
    pageSizeOptions = [5, 10, 20, 50],
    color = 'primary',
}) => {
    const handlePageChange = (event, newPage) => {
        // MUI Pagination is 1-indexed, API is 0-indexed
        onPageChange(newPage - 1);
    };

    const handleSizeChange = (event) => {
        onSizeChange(event.target.value);
        onPageChange(0); // Reset to first page when changing size
    };

    // Calculate display range
    const startItem = page * size + 1;
    const endItem = Math.min((page + 1) * size, totalElements);

    if (totalElements === 0) {
        return null;
    }

    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: { xs: 'column', sm: 'row' },
                justifyContent: 'space-between',
                alignItems: 'center',
                gap: 2,
                mt: 3,
                mb: 2,
            }}
        >
            {/* Items count display */}
            <Typography variant="body2" color="text.secondary">
                Showing {startItem}-{endItem} of {totalElements} items
            </Typography>

            {/* Page size selector */}
            {showPageSize && (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <Typography variant="body2" color="text.secondary">
                        Items per page:
                    </Typography>
                    <FormControl size="small" variant="outlined">
                        <Select
                            value={size}
                            onChange={handleSizeChange}
                            sx={{ minWidth: 70 }}
                        >
                            {pageSizeOptions.map((option) => (
                                <MenuItem key={option} value={option}>
                                    {option}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Box>
            )}

            {/* Pagination controls */}
            <MuiPagination
                count={totalPages}
                page={page + 1} // MUI Pagination is 1-indexed
                onChange={handlePageChange}
                color={color}
                showFirstButton
                showLastButton
                siblingCount={1}
                boundaryCount={1}
            />
        </Box>
    );
};

export default Pagination;

