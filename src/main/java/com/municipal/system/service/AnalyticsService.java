package com.municipal.system.service;

import com.municipal.system.dto.ZoneAnalyticsResponse;
import com.municipal.system.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final ComplaintRepository complaintRepository;

    public AnalyticsService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    public List<ZoneAnalyticsResponse> getZoneAnalytics() {
        List<Object[]> rows = complaintRepository.getZoneAnalyticsData();
        List<ZoneAnalyticsResponse> responses = new ArrayList<>();

        for (Object[] row : rows) {
            String zoneId = (String) row[0];
            Long totalCount = ((Number) row[1]).longValue();
            Double avgScore = row[2] != null ? ((Number) row[2]).doubleValue() : 0.0;
            Long resolvedCount = row[3] != null ? ((Number) row[3]).longValue() : 0L;

            responses.add(new ZoneAnalyticsResponse(zoneId, totalCount, resolvedCount, avgScore));
        }

        return responses;
    }

    public Map<String, Object> getOverallSummary() {
        long totalComplaints = complaintRepository.count();
        List<ZoneAnalyticsResponse> zones = getZoneAnalytics();

        long resolvedTotal = zones.stream().mapToLong(ZoneAnalyticsResponse::getResolvedComplaints).sum();
        long pendingTotal = totalComplaints - resolvedTotal;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalComplaints", totalComplaints);
        summary.put("resolvedTotal", resolvedTotal);
        summary.put("pendingTotal", pendingTotal);
        summary.put("overallResolutionRatePercentage", totalComplaints > 0
                ? Math.round(((double) resolvedTotal / totalComplaints * 100.0) * 100.0) / 100.0
                : 0.0);
        summary.put("zonesTrackedCount", zones.size());
        summary.put("zoneBreakdown", zones);

        return summary;
    }
}
