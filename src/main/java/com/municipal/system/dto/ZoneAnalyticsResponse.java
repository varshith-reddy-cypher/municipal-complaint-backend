package com.municipal.system.dto;

public class ZoneAnalyticsResponse {

    private String zoneId;
    private Long totalComplaints;
    private Long resolvedComplaints;
    private Long pendingComplaints;
    private Double averagePriorityScore;
    private Double resolutionRatePercentage;

    public ZoneAnalyticsResponse() {
    }

    public ZoneAnalyticsResponse(String zoneId, Long totalComplaints, Long resolvedComplaints,
                                 Double averagePriorityScore) {
        this.zoneId = zoneId;
        this.totalComplaints = totalComplaints != null ? totalComplaints : 0L;
        this.resolvedComplaints = resolvedComplaints != null ? resolvedComplaints : 0L;
        this.pendingComplaints = this.totalComplaints - this.resolvedComplaints;
        this.averagePriorityScore = averagePriorityScore != null ? Math.round(averagePriorityScore * 100.0) / 100.0 : 0.0;
        this.resolutionRatePercentage = this.totalComplaints > 0
                ? Math.round(((double) this.resolvedComplaints / this.totalComplaints * 100.0) * 100.0) / 100.0
                : 0.0;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Long getTotalComplaints() {
        return totalComplaints;
    }

    public void setTotalComplaints(Long totalComplaints) {
        this.totalComplaints = totalComplaints;
    }

    public Long getResolvedComplaints() {
        return resolvedComplaints;
    }

    public void setResolvedComplaints(Long resolvedComplaints) {
        this.resolvedComplaints = resolvedComplaints;
    }

    public Long getPendingComplaints() {
        return pendingComplaints;
    }

    public void setPendingComplaints(Long pendingComplaints) {
        this.pendingComplaints = pendingComplaints;
    }

    public Double getAveragePriorityScore() {
        return averagePriorityScore;
    }

    public void setAveragePriorityScore(Double averagePriorityScore) {
        this.averagePriorityScore = averagePriorityScore;
    }

    public Double getResolutionRatePercentage() {
        return resolutionRatePercentage;
    }

    public void setResolutionRatePercentage(Double resolutionRatePercentage) {
        this.resolutionRatePercentage = resolutionRatePercentage;
    }
}
