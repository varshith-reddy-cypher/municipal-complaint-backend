package com.municipal.system.service;

import com.municipal.system.dto.AssignOfficerRequest;
import com.municipal.system.dto.ComplaintResponse;
import com.municipal.system.dto.CreateComplaintRequest;
import com.municipal.system.dto.StatusLogResponse;
import com.municipal.system.dto.UpdateStatusRequest;
import com.municipal.system.model.*;
import com.municipal.system.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final StatusLogRepository statusLogRepository;
    private final PriorityAlgorithmService priorityAlgorithmService;

    public ComplaintService(ComplaintRepository complaintRepository,
                            UserRepository userRepository,
                            DepartmentRepository departmentRepository,
                            AssignmentRepository assignmentRepository,
                            StatusLogRepository statusLogRepository,
                            PriorityAlgorithmService priorityAlgorithmService) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.statusLogRepository = statusLogRepository;
        this.priorityAlgorithmService = priorityAlgorithmService;
    }

    public ComplaintResponse createComplaint(CreateComplaintRequest request) {
        User citizen = userRepository.findById(request.getCitizenId())
                .orElseThrow(() -> new IllegalArgumentException("Citizen not found with ID: " + request.getCitizenId()));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Department not found with ID: " + request.getDepartmentId()));

        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCitizen(citizen);
        complaint.setDepartment(department);
        complaint.setZoneId(request.getZoneId());
        complaint.setLatitude(request.getLatitude());
        complaint.setLongitude(request.getLongitude());
        complaint.setPhotoUrl(request.getPhotoUrl());
        complaint.setSeverityLevel(request.getSeverityLevel());
        complaint.setAffectedCount(request.getAffectedCount() != null ? Math.max(1, request.getAffectedCount()) : 1);
        complaint.setIsCriticalHazard(Boolean.TRUE.equals(request.getIsCriticalHazard()));
        complaint.setStatus(ComplaintStatus.REPORTED);
        complaint.setCreatedAt(LocalDateTime.now());
        complaint.setUpdatedAt(LocalDateTime.now());

        double initialScore = priorityAlgorithmService.calculateScore(complaint);
        complaint.setPriorityScore(initialScore);

        Complaint savedComplaint = complaintRepository.save(complaint);

        StatusLog initialLog = new StatusLog(
                savedComplaint,
                ComplaintStatus.REPORTED,
                "Complaint filed by citizen with initial priority score: " + initialScore,
                request.getPhotoUrl(),
                citizen
        );
        statusLogRepository.save(initialLog);

        return enrichComplaintResponse(savedComplaint);
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getPriorityQueue(boolean onlyActive) {
        List<Complaint> complaints = onlyActive
                ? complaintRepository.findByStatusNotOrderByPriorityScoreDesc(ComplaintStatus.RESOLVED)
                : complaintRepository.findAllByOrderByPriorityScoreDesc();

        return complaints.stream()
                .map(this::enrichComplaintResponse)
                .collect(Collectors.toList());
    }

    public ComplaintResponse upvoteComplaint(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + complaintId));

        complaint.setAffectedCount(complaint.getAffectedCount() + 1);
        complaint.setUpdatedAt(LocalDateTime.now());

        double newScore = priorityAlgorithmService.calculateScore(complaint);
        complaint.setPriorityScore(newScore);

        Complaint saved = complaintRepository.save(complaint);
        return enrichComplaintResponse(saved);
    }

    public ComplaintResponse assignOfficer(Long complaintId, AssignOfficerRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + complaintId));

        User officer = userRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new IllegalArgumentException("Officer not found with ID: " + request.getOfficerId()));

        if (officer.getRole() != Role.OFFICER && officer.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Target user is not a field officer or administrator");
        }

        Assignment assignment = new Assignment(complaint, officer, request.getNotes());
        assignmentRepository.save(assignment);

        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaint.setUpdatedAt(LocalDateTime.now());
        complaintRepository.save(complaint);

        StatusLog log = new StatusLog(
                complaint,
                ComplaintStatus.ASSIGNED,
                "Assigned to field officer: " + officer.getName() + ". Notes: " + (request.getNotes() != null ? request.getNotes() : "N/A"),
                null,
                officer
        );
        statusLogRepository.save(log);

        return enrichComplaintResponse(complaint);
    }

    public ComplaintResponse autoDispatch(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + complaintId));

        List<User> zoneOfficers = userRepository.findByRoleAndZoneId(Role.OFFICER, complaint.getZoneId());
        User assignedOfficer;
        if (!zoneOfficers.isEmpty()) {
            assignedOfficer = zoneOfficers.get(0);
        } else {
            List<User> allOfficers = userRepository.findByRole(Role.OFFICER);
            if (allOfficers.isEmpty()) {
                throw new IllegalStateException("No active field officers available in the system");
            }
            assignedOfficer = allOfficers.get(0);
        }

        AssignOfficerRequest assignRequest = new AssignOfficerRequest(
                assignedOfficer.getUserId(),
                "Smart Dispatch: Auto-routed based on zone (" + complaint.getZoneId() + ") & department (" + complaint.getDepartment().getDeptName() + ")"
        );
        return assignOfficer(complaintId, assignRequest);
    }

    public ComplaintResponse updateStatus(Long complaintId, UpdateStatusRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + complaintId));

        ComplaintStatus oldStatus = complaint.getStatus();
        complaint.setStatus(request.getStatus());
        complaint.setUpdatedAt(LocalDateTime.now());

        if (request.getStatus() == ComplaintStatus.RESOLVED) {
            complaint.setPriorityScore(0.0);
        }

        complaintRepository.save(complaint);

        User updater = null;
        if (request.getUpdatedByUserId() != null) {
            updater = userRepository.findById(request.getUpdatedByUserId()).orElse(null);
        }

        StatusLog log = new StatusLog(
                complaint,
                request.getStatus(),
                "Status changed from " + oldStatus + " to " + request.getStatus() +
                        (request.getRemarks() != null ? ". Remarks: " + request.getRemarks() : ""),
                request.getPhotoUrl(),
                updater
        );
        statusLogRepository.save(log);

        return enrichComplaintResponse(complaint);
    }

    @Transactional(readOnly = true)
    public ComplaintResponse getComplaintById(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found with ID: " + complaintId));
        return enrichComplaintResponse(complaint);
    }

    @Transactional(readOnly = true)
    public List<StatusLogResponse> getComplaintTimeline(Long complaintId) {
        return statusLogRepository.findByComplaint_ComplaintIdOrderByUpdatedAtAsc(complaintId)
                .stream()
                .map(StatusLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> filterComplaints(String zoneId, Long deptId, ComplaintStatus status) {
        List<Complaint> list;
        if (status != null) {
            list = complaintRepository.findByStatus(status);
        } else if (zoneId != null) {
            list = complaintRepository.findByZoneId(zoneId);
        } else if (deptId != null) {
            list = complaintRepository.findByDepartment_DeptId(deptId);
        } else {
            list = complaintRepository.findAll();
        }

        return list.stream()
                .map(this::enrichComplaintResponse)
                .collect(Collectors.toList());
    }

    private ComplaintResponse enrichComplaintResponse(Complaint complaint) {
        ComplaintResponse dto = ComplaintResponse.fromEntity(complaint);
        Optional<Assignment> latestAssignment = assignmentRepository
                .findTopByComplaint_ComplaintIdOrderByAssignedAtDesc(complaint.getComplaintId());

        if (latestAssignment.isPresent()) {
            User officer = latestAssignment.get().getOfficer();
            dto.setAssignedOfficerId(officer.getUserId());
            dto.setAssignedOfficerName(officer.getName());
        }
        return dto;
    }
}
