package com.municipal.system.dto;

import com.municipal.system.model.Complaint;
import com.municipal.system.model.ComplaintStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class ComplaintResponse {

    private Long complaintId;
    private String title;
    private String description;
    private Long citizenId;
    private String citizenName;
    private Long departmentId;
    private String departmentName;
    private String zoneId;
    private Double latitude;
    private Double longitude;
    private String photoUrl;
    private Integer severityLevel;
    private Integer affectedCount;
    private Double priorityScore;
    private Boolean isCriticalHazard;
    private ComplaintStatus status;
    private Long assignedOfficerId;
    private String assignedOfficerName;
    private Double ageInHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ComplaintResponse() {
    }

    public static ComplaintResponse fromEntity(Complaint complaint) {
        ComplaintResponse dto = new ComplaintResponse();
        dto.setComplaintId(complaint.getComplaintId());
        dto.setTitle(complaint.getTitle());
        dto.setDescription(complaint.getDescription());
        if (complaint.getCitizen() != null) {
            dto.setCitizenId(complaint.getCitizen().getUserId());
            dto.setCitizenName(complaint.getCitizen().getName());
        }
        if (complaint.getDepartment() != null) {
            dto.setDepartmentId(complaint.getDepartment().getDeptId());
            dto.setDepartmentName(complaint.getDepartment().getDeptName());
        }
        dto.setZoneId(complaint.getZoneId());
        dto.setLatitude(complaint.getLatitude());
        dto.setLongitude(complaint.getLongitude());
        dto.setPhotoUrl(complaint.getPhotoUrl());
        dto.setSeverityLevel(complaint.getSeverityLevel());
        dto.setAffectedCount(complaint.getAffectedCount());
        dto.setPriorityScore(Math.round(complaint.getPriorityScore() * 100.0) / 100.0);
        dto.setIsCriticalHazard(complaint.getIsCriticalHazard());
        dto.setStatus(complaint.getStatus());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());

        if (complaint.getCreatedAt() != null) {
            long minutes = Duration.between(complaint.getCreatedAt(), LocalDateTime.now()).toMinutes();
            dto.setAgeInHours(Math.round((minutes / 60.0) * 100.0) / 100.0);
        }
        return dto;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Long citizenId) {
        this.citizenId = citizenId;
    }

    public String getCitizenName() {
        return citizenName;
    }

    public void setCitizenName(String citizenName) {
        this.citizenName = citizenName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Integer getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(Integer severityLevel) {
        this.severityLevel = severityLevel;
    }

    public Integer getAffectedCount() {
        return affectedCount;
    }

    public void setAffectedCount(Integer affectedCount) {
        this.affectedCount = affectedCount;
    }

    public Double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(Double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public Boolean getIsCriticalHazard() {
        return isCriticalHazard;
    }

    public void setIsCriticalHazard(Boolean isCriticalHazard) {
        this.isCriticalHazard = isCriticalHazard;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public Long getAssignedOfficerId() {
        return assignedOfficerId;
    }

    public void setAssignedOfficerId(Long assignedOfficerId) {
        this.assignedOfficerId = assignedOfficerId;
    }

    public String getAssignedOfficerName() {
        return assignedOfficerName;
    }

    public void setAssignedOfficerName(String assignedOfficerName) {
        this.assignedOfficerName = assignedOfficerName;
    }

    public Double getAgeInHours() {
        return ageInHours;
    }

    public void setAgeInHours(Double ageInHours) {
        this.ageInHours = ageInHours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
