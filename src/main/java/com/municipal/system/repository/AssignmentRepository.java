package com.municipal.system.repository;

import com.municipal.system.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    Optional<Assignment> findTopByComplaint_ComplaintIdOrderByAssignedAtDesc(Long complaintId);
    List<Assignment> findByOfficer_UserId(Long officerId);
    List<Assignment> findByComplaint_ComplaintId(Long complaintId);
}
