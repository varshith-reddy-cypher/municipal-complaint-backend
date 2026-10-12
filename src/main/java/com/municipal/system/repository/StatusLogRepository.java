package com.municipal.system.repository;

import com.municipal.system.model.StatusLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusLogRepository extends JpaRepository<StatusLog, Long> {
    List<StatusLog> findByComplaint_ComplaintIdOrderByUpdatedAtAsc(Long complaintId);
}
