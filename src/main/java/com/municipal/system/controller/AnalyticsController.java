package com.municipal.system.controller;

import com.municipal.system.dto.ApiResponse;
import com.municipal.system.dto.ZoneAnalyticsResponse;
import com.municipal.system.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Analytics & Reports", description = "Municipal administration analytics, zone statistics, and SLAs")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get zone analytics & stats")
    public ResponseEntity<ApiResponse<List<ZoneAnalyticsResponse>>> getZoneAnalytics() {
        List<ZoneAnalyticsResponse> zoneStats = analyticsService.getZoneAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Zone analytics fetched successfully", zoneStats));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get overall municipal turnaround summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverallSummary() {
        Map<String, Object> summary = analyticsService.getOverallSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
