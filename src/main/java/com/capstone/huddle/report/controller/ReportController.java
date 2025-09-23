package com.capstone.huddle.report.controller;

import com.capstone.huddle.report.dto.request.CreateReportDto;
import com.capstone.huddle.report.dto.request.UpdateReportDto;
import com.capstone.huddle.report.dto.response.ReportResponse;
import com.capstone.huddle.report.dto.response.ReportResponseDto;
import com.capstone.huddle.report.entity.ReportStatus;
import com.capstone.huddle.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/huddle/reports")
@Tag(name = "Reports", description = "Report management APIs")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create report", description = "Create a new report for an article")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created report"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Article not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> createReport(@Valid @RequestBody CreateReportDto dto,
                                          Authentication authentication) {
        try {
            String username = authentication.getName();
            ReportResponseDto createdReport = reportService.createReport(dto, username);

            ReportResponse<ReportResponseDto> response = ReportResponse.<ReportResponseDto>builder()
                    .success(true)
                    .message("Report created successfully")
                    .data(createdReport)
                    .build();

            return ResponseEntity.status(201).body(response);
        } catch (EntityNotFoundException e) {
            log.error("Entity not found: ", e);
            ReportResponse<Object> errorResponse = ReportResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(errorResponse);
        } catch (IllegalStateException e) {
            log.error("Invalid state: ", e);
            ReportResponse<Object> errorResponse = ReportResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            log.error("Error creating report: ", e);
            ReportResponse<Object> errorResponse = ReportResponse.builder()
                    .success(false)
                    .message("Failed to create report: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all reports", description = "Retrieve all reports with optional status filter and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved reports"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> getAllReports(@RequestParam(required = false) ReportStatus status,
                                           Pageable pageable) {
        try {
            Page<ReportResponseDto> reports = reportService.getAllReports(status, pageable);

            ReportResponse<Page<ReportResponseDto>> response = ReportResponse.<Page<ReportResponseDto>>builder()
                    .success(true)
                    .message("Reports retrieved successfully")
                    .data(reports)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving reports: ", e);
            ReportResponse<Object> errorResponse = ReportResponse.builder()
                    .success(false)
                    .message("Failed to retrieve reports: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update report", description = "Update report status and add admin notes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated report"),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - admin access required"),
            @ApiResponse(responseCode = "404", description = "Report not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> updateReport(@PathVariable UUID id,
                                          @Valid @RequestBody UpdateReportDto dto,
                                          Authentication authentication) {
        try {
            String adminUsername = authentication.getName();
            ReportResponseDto updatedReport = reportService.updateReportStatus(id, dto, adminUsername);

            ReportResponse<ReportResponseDto> response = ReportResponse.<ReportResponseDto>builder()
                    .success(true)
                    .message("Report updated successfully")
                    .data(updatedReport)
                    .build();

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            log.error("Report not found: ", e);
            ReportResponse<Object> errorResponse = ReportResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(errorResponse);
        } catch (Exception e) {
            log.error("Error updating report: ", e);
            ReportResponse<Object> errorResponse = ReportResponse.builder()
                    .success(false)
                    .message("Failed to update report: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/debug/auth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> debugAuth(Authentication authentication) {
        var authorities = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList());

        log.info("Current user: {}, Authorities: {}", authentication.getName(), authorities);

        return ResponseEntity.ok(Map.of(
                "username", authentication.getName(),
                "authorities", authorities,
                "principal", authentication.getPrincipal()
        ));
    }

}
