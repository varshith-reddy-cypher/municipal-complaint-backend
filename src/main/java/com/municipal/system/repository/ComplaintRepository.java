package com.municipal.system.repository;

import com.municipal.system.model.Complaint;
import com.municipal.system.model.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findAllByOrderByPriorityScoreDesc();

    List<Complaint> findByStatusNotOrderByPriorityScoreDesc(ComplaintStatus status);

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByCitizen_UserIdOrderByCreatedAtDesc(Long citizenId);

    List<Complaint> findByZoneId(String zoneId);

    List<Complaint> findByDepartment_DeptId(Long departmentId);

    @Query("SELECT c FROM Complaint c WHERE c.status <> 'RESOLVED'")
    List<Complaint> findActiveComplaintsForRecalculation();

    @Query("SELECT c.zoneId, COUNT(c), AVG(c.priorityScore), " +
           "SUM(CASE WHEN c.status = 'RESOLVED' THEN 1 ELSE 0 END) " +
           "FROM Complaint c GROUP BY c.zoneId")
    List<Object[]> getZoneAnalyticsData();
}
