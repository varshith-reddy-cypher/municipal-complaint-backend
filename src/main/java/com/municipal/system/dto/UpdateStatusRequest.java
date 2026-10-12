package com.municipal.system.dto;

import com.municipal.system.model.ComplaintStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

    @NotNull(message = "New status is required")
    private ComplaintStatus status;

    private String remarks;

    private String photoUrl;

    private Long updatedByUserId;

    public UpdateStatusRequest() {
    }

    public UpdateStatusRequest(ComplaintStatus status, String remarks, String photoUrl, Long updatedByUserId) {
        this.status = status;
        this.remarks = remarks;
        this.photoUrl = photoUrl;
        this.updatedByUserId = updatedByUserId;
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
}
