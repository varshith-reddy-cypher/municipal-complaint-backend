package com.municipal.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateComplaintRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Citizen ID is required")
    private Long citizenId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotBlank(message = "Zone ID is required (e.g. ZONE-NORTH, ZONE-SOUTH)")
    private String zoneId;

    @NotNull(message = "Latitude is required for geo-tagging")
    private Double latitude;

    @NotNull(message = "Longitude is required for geo-tagging")
    private Double longitude;

    private String photoUrl;

    @NotNull(message = "Severity level is required (1 to 5)")
    @Min(value = 1, message = "Severity level must be at least 1")
    @Max(value = 5, message = "Severity level cannot exceed 5")
    private Integer severityLevel;

    private Integer affectedCount = 1;

    private Boolean isCriticalHazard = false;

    public CreateComplaintRequest() {
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

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
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

    public Boolean getIsCriticalHazard() {
        return isCriticalHazard;
    }

    public void setIsCriticalHazard(Boolean isCriticalHazard) {
        this.isCriticalHazard = isCriticalHazard;
    }
}
