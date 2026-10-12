package com.municipal.system.service;

import com.municipal.system.dto.AssignOfficerRequest;
import com.municipal.system.dto.ComplaintResponse;
import com.municipal.system.dto.CreateComplaintRequest;
import com.municipal.system.dto.UpdateStatusRequest;
import com.municipal.system.model.*;
import com.municipal.system.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private AssignmentRepository assignmentRepository;
    @Mock
    private StatusLogRepository statusLogRepository;
    @Mock
    private PriorityAlgorithmService priorityAlgorithmService;

    @InjectMocks
    private ComplaintService complaintService;

    private User citizen;
    private User officer;
    private Department department;

    @BeforeEach
    void setUp() {
        citizen = new User("John Citizen", "john@example.com", Role.CITIZEN, "ZONE-NORTH");
        citizen.setUserId(1L);

        officer = new User("Officer Kumar", "officer@municipal.gov", Role.OFFICER, "ZONE-NORTH");
        officer.setUserId(2L);

        department = new Department("Roads", officer.getUserId());
        department.setDeptId(10L);
    }

    @Test
    @DisplayName("Submit complaint calculates priority score and saves audit log")
    void testCreateComplaint() {
        CreateComplaintRequest request = new CreateComplaintRequest();
        request.setTitle("Pothole on Ring Road");
        request.setCitizenId(1L);
        request.setDepartmentId(10L);
        request.setZoneId("ZONE-NORTH");
        request.setLatitude(12.97);
        request.setLongitude(77.59);
        request.setSeverityLevel(4);
        request.setAffectedCount(5);

        when(userRepository.findById(1L)).thenReturn(Optional.of(citizen));
        when(departmentRepository.findById(10L)).thenReturn(Optional.of(department));
        when(priorityAlgorithmService.calculateScore(any())).thenReturn(3.5);

        when(complaintRepository.save(any(Complaint.class))).thenAnswer(invocation -> {
            Complaint c = invocation.getArgument(0);
            c.setComplaintId(100L);
            return c;
        });

        ComplaintResponse response = complaintService.createComplaint(request);

        assertNotNull(response);
        assertEquals(100L, response.getComplaintId());
        assertEquals("Pothole on Ring Road", response.getTitle());
        assertEquals(3.5, response.getPriorityScore());
        assertEquals(ComplaintStatus.REPORTED, response.getStatus());
        verify(statusLogRepository, times(1)).save(any(StatusLog.class));
    }

    @Test
    @DisplayName("Upvote increments affected count and recalculates score")
    void testUpvoteComplaint() {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(100L);
        complaint.setCitizen(citizen);
        complaint.setDepartment(department);
        complaint.setAffectedCount(3);
        complaint.setSeverityLevel(3);
        complaint.setZoneId("ZONE-NORTH");

        when(complaintRepository.findById(100L)).thenReturn(Optional.of(complaint));
        when(priorityAlgorithmService.calculateScore(complaint)).thenReturn(4.2);
        when(complaintRepository.save(any(Complaint.class))).thenReturn(complaint);

        ComplaintResponse response = complaintService.upvoteComplaint(100L);

        assertEquals(4, complaint.getAffectedCount());
        assertEquals(4.2, response.getPriorityScore());
    }

    @Test
    @DisplayName("Assign officer transitions status to ASSIGNED")
    void testAssignOfficer() {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(100L);
        complaint.setCitizen(citizen);
        complaint.setDepartment(department);
        complaint.setZoneId("ZONE-NORTH");
        complaint.setStatus(ComplaintStatus.REPORTED);

        when(complaintRepository.findById(100L)).thenReturn(Optional.of(complaint));
        when(userRepository.findById(2L)).thenReturn(Optional.of(officer));

        AssignOfficerRequest request = new AssignOfficerRequest(2L, "Immediate inspection required");
        ComplaintResponse response = complaintService.assignOfficer(100L, request);

        assertEquals(ComplaintStatus.ASSIGNED, response.getStatus());
        verify(assignmentRepository, times(1)).save(any(Assignment.class));
        verify(statusLogRepository, times(1)).save(any(StatusLog.class));
    }

    @Test
    @DisplayName("Update status progresses through resolution pipeline")
    void testUpdateStatusToResolved() {
        Complaint complaint = new Complaint();
        complaint.setComplaintId(100L);
        complaint.setCitizen(citizen);
        complaint.setDepartment(department);
        complaint.setZoneId("ZONE-NORTH");
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);

        when(complaintRepository.findById(100L)).thenReturn(Optional.of(complaint));
        when(complaintRepository.save(any(Complaint.class))).thenReturn(complaint);

        UpdateStatusRequest request = new UpdateStatusRequest(
                ComplaintStatus.RESOLVED,
                "Pothole filled and road repaved",
                "https://example.com/resolved-road.jpg",
                2L
        );

        ComplaintResponse response = complaintService.updateStatus(100L, request);

        assertEquals(ComplaintStatus.RESOLVED, response.getStatus());
        assertEquals(0.0, response.getPriorityScore());
        verify(statusLogRepository, times(1)).save(any(StatusLog.class));
    }
}
