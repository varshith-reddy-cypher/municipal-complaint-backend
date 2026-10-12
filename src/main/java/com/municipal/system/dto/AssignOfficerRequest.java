package com.municipal.system.dto;

import jakarta.validation.constraints.NotNull;

public class AssignOfficerRequest {

    @NotNull(message = "Officer ID is required")
    private Long officerId;

    private String notes;

    public AssignOfficerRequest() {
    }

    public AssignOfficerRequest(Long officerId, String notes) {
        this.officerId = officerId;
        this.notes = notes;
    }

    public Long getOfficerId() {
        return officerId;
    }

    public void setOfficerId(Long officerId) {
        this.officerId = officerId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
