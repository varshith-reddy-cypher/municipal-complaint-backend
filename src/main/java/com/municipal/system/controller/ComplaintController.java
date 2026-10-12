package com.municipal.system.controller;

import com.municipal.system.dto.*;
import com.municipal.system.model.ComplaintStatus;
import com.municipal.system.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@Tag(name = "Complaints Management", description = "Endpoints for reporting, scoring, dispatching, and tracking civic issues")
@CrossOrigin(origins = "*")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping
    @Operation(summary = "Submit new civic complaint ticket")
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(
            @Valid @RequestBody CreateComplaintRequest request) {
        ComplaintResponse created = complaintService.createComplaint(request);
        return new ResponseEntity<>(ApiResponse.success("Complaint submitted successfully with priority score: " + created.getPriorityScore(), created), HttpStatus.CREATED);
    }

    @GetMapping("/priority")
    @Operation(summary = "Fetch dynamically sorted priority queue")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getPriorityQueue(
            @Parameter(description = "If true, returns only active (unresolved) tickets. Defaults to true.")
            @RequestParam(defaultValue = "true") boolean onlyActive) {
        List<ComplaintResponse> queue = complaintService.getPriorityQueue(onlyActive);
        return ResponseEntity.ok(ApiResponse.success(queue));
    }

    @PutMapping("/{id}/assign")
    @Operation(summary = "Assign complaint to field officer")
    public ResponseEntity<ApiResponse<ComplaintResponse>> assignOfficer(
            @PathVariable("id") Long complaintId,
            @Valid @RequestBody AssignOfficerRequest request) {
        ComplaintResponse updated = complaintService.assignOfficer(complaintId, request);
        return ResponseEntity.ok(ApiResponse.success("Complaint successfully assigned to officer", updated));
    }

    @PostMapping("/{id}/auto-dispatch")
    @Operation(summary = "Smart auto-dispatch to active zone officer")
    public ResponseEntity<ApiResponse<ComplaintResponse>> autoDispatch(
            @PathVariable("id") Long complaintId) {
        ComplaintResponse updated = complaintService.autoDispatch(complaintId);
        return ResponseEntity.ok(ApiResponse.success("Complaint auto-dispatched to zone officer", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update resolution stage")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateStatus(
            @PathVariable("id") Long complaintId,
            @Valid @RequestBody UpdateStatusRequest request) {
        ComplaintResponse updated = complaintService.updateStatus(complaintId, request);
        return ResponseEntity.ok(ApiResponse.success("Status successfully updated to " + request.getStatus(), updated));
    }

    @PostMapping("/{id}/upvote")
    @Operation(summary = "Upvote existing complaint")
    public ResponseEntity<ApiResponse<ComplaintResponse>> upvoteComplaint(
            @PathVariable("id") Long complaintId) {
        ComplaintResponse updated = complaintService.upvoteComplaint(complaintId);
        return ResponseEntity.ok(ApiResponse.success("Complaint upvoted! New priority score: " + updated.getPriorityScore(), updated));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get complaint details by ID")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaintById(
            @PathVariable("id") Long complaintId) {
        ComplaintResponse complaint = complaintService.getComplaintById(complaintId);
        return ResponseEntity.ok(ApiResponse.success(complaint));
    }

    @GetMapping("/{id}/timeline")
    @Operation(summary = "Get complaint resolution timeline & audit trail")
    public ResponseEntity<ApiResponse<List<StatusLogResponse>>> getComplaintTimeline(
            @PathVariable("id") Long complaintId) {
        List<StatusLogResponse> timeline = complaintService.getComplaintTimeline(complaintId);
        return ResponseEntity.ok(ApiResponse.success(timeline));
    }

    @GetMapping
    @Operation(summary = "Filter complaints by zone, department, or status")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> filterComplaints(
            @RequestParam(required = false) String zoneId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) ComplaintStatus status) {
        List<ComplaintResponse> list = complaintService.filterComplaints(zoneId, departmentId, status);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
