package com.capstone.huddle.report.controller;

import com.capstone.huddle.report.dto.request.CreateReportDto;
import com.capstone.huddle.report.dto.request.UpdateReportDto;
import com.capstone.huddle.report.dto.response.ReportDto;
import com.capstone.huddle.report.dto.response.ReportResponse;
import com.capstone.huddle.report.entity.ReportEntity;
import com.capstone.huddle.report.entity.ReportStatus;
import com.capstone.huddle.report.mapper.ReportMapper;
import com.capstone.huddle.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/huddle/reports")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;
    private final ReportMapper reportMapper;

    @PostMapping("/create-report")
    @Operation(summary = "Create Report", description = "Endpoint for users to create a report against an article or user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Report created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ReportResponse<ReportDto>> createReport(
            @Valid @RequestBody CreateReportDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Creating report for user: {}", userDetails.getUsername());
        try {
            ReportEntity report = reportService.createReport(dto, userDetails.getUsername());
            ReportDto reportDto = reportMapper.toDto(report);

            ReportResponse<ReportDto> response = ReportResponse.<ReportDto>builder()
                    .success(true)
                    .message("Report created successfully")
                    .data(reportDto)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Invalid input for report creation: {}", e.getMessage());
            ReportResponse<ReportDto> response = ReportResponse.<ReportDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("Failed to create report", e);
            ReportResponse<ReportDto> response = ReportResponse.<ReportDto>builder()
                    .success(false)
                    .message("Failed to create report: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/all-reports")
    @Operation(summary = "Get All Reports", description = "Admin endpoint to retrieve all reports with optional status filtering and pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reports retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, admin access required"),
            @ApiResponse(responseCode = "403", description = "Forbidden, admin role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse<Page<ReportDto>>> getAllReports(
            @RequestParam(required = false) ReportStatus status,
            Pageable pageable) {
        log.info("Fetching all reports with status: {} and pageable: {}", status, pageable);
        try {
            Page<ReportEntity> reportEntities = reportService.getAllReports(status, pageable);

            // Convert entities to DTOs
            List<ReportDto> reportDtos = reportEntities.getContent().stream()
                    .map(reportMapper::toDto)
                    .collect(Collectors.toList());

            Page<ReportDto> reportDtoPage = new PageImpl<>(
                    reportDtos,
                    pageable,
                    reportEntities.getTotalElements()
            );

            ReportResponse<Page<ReportDto>> response = ReportResponse.<Page<ReportDto>>builder()
                    .success(true)
                    .message("Reports retrieved successfully")
                    .data(reportDtoPage)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to retrieve reports", e);
            ReportResponse<Page<ReportDto>> response = ReportResponse.<Page<ReportDto>>builder()
                    .success(false)
                    .message("Failed to retrieve reports: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{reportId}")
    @Operation(summary = "Update Report Status", description = "Admin endpoint to update the status of a specific report.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, admin access required"),
            @ApiResponse(responseCode = "403", description = "Forbidden, admin role required"),
            @ApiResponse(responseCode = "404", description = "Report not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportResponse<ReportDto>> updateReport(
            @PathVariable UUID reportId,
            @Valid @RequestBody UpdateReportDto dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Updating report {} by admin: {}", reportId, userDetails.getUsername());
        try {
            ReportEntity report = reportService.updateReportStatus(reportId, dto, userDetails.getUsername());
            ReportDto reportDto = reportMapper.toDto(report);

            ReportResponse<ReportDto> response = ReportResponse.<ReportDto>builder()
                    .success(true)
                    .message("Report status updated successfully")
                    .data(reportDto)
                    .build();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Report not found with id: {}", reportId);
            ReportResponse<ReportDto> response = ReportResponse.<ReportDto>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.error("Failed to update report", e);
            ReportResponse<ReportDto> response = ReportResponse.<ReportDto>builder()
                    .success(false)
                    .message("Failed to update report: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test")
    @Operation(summary = "Test endpoint", description = "Simple test to verify controller is working")
    @ApiResponse(responseCode = "200", description = "Controller is working")
    public ResponseEntity<String> test() {
        log.info("Test endpoint called");
        return ResponseEntity.ok("Report controller is working");
    }
}
