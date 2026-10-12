package com.municipal.system.dto;

import com.municipal.system.model.ComplaintStatus;
import com.municipal.system.model.StatusLog;

import java.time.LocalDateTime;

public class StatusLogResponse {

    private Long logId;
    private ComplaintStatus status;
    private String remarks;
    private String photoUrl;
    private Long updatedByUserId;
    private String updatedByName;
    private LocalDateTime updatedAt;

    public StatusLogResponse() {
    }

    public static StatusLogResponse fromEntity(StatusLog log) {
        StatusLogResponse dto = new StatusLogResponse();
        dto.setLogId(log.getLogId());
        dto.setStatus(log.getStatus());
        dto.setRemarks(log.getRemarks());
        dto.setPhotoUrl(log.getPhotoUrl());
        if (log.getUpdatedBy() != null) {
            dto.setUpdatedByUserId(log.getUpdatedBy().getUserId());
            dto.setUpdatedByName(log.getUpdatedBy().getName());
        }
        dto.setUpdatedAt(log.getUpdatedAt());
        return dto;
    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getUpdatedByUserId() {
        return updatedByUserId;
    }

    public void setUpdatedByUserId(Long updatedByUserId) {
        this.updatedByUserId = updatedByUserId;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
