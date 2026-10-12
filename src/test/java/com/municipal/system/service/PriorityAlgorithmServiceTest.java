package com.municipal.system.service;

import com.municipal.system.model.Complaint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriorityAlgorithmServiceTest {

    private PriorityAlgorithmService priorityService;

    @BeforeEach
    void setUp() {
        priorityService = new PriorityAlgorithmService();
        ReflectionTestUtils.setField(priorityService, "w1Severity", 0.5);
        ReflectionTestUtils.setField(priorityService, "w2Affected", 0.3);
        ReflectionTestUtils.setField(priorityService, "w3Age", 0.2);
        ReflectionTestUtils.setField(priorityService, "hazardBoost", 50.0);
    }

    @Test
    @DisplayName("Calculate score with standard parameters: Severity 4, 10 affected, 5 hours old")
    void testStandardCalculation() {
        Complaint complaint = new Complaint();
        complaint.setSeverityLevel(4);
        complaint.setAffectedCount(10);
        complaint.setIsCriticalHazard(false);
        complaint.setCreatedAt(LocalDateTime.now().minusHours(5));

        // (4 * 0.5) + (10 * 0.3) + (5 * 0.2) = 2.0 + 3.0 + 1.0 = 6.0
        double score = priorityService.calculateScore(complaint);
        assertEquals(6.0, score, 0.1);
    }

    @Test
    @DisplayName("Verify Critical Hazard +50 boost overrides regular scores")
    void testCriticalSafetyHazardBoost() {
        Complaint complaint = new Complaint();
        complaint.setSeverityLevel(5);
        complaint.setAffectedCount(20);
        complaint.setIsCriticalHazard(true);
        complaint.setCreatedAt(LocalDateTime.now().minusHours(2));

        // Base: (5 * 0.5) + (20 * 0.3) + (2 * 0.2) = 8.9 + 50 = 58.9
        double score = priorityService.calculateScore(complaint);
        assertTrue(score >= 58.5 && score <= 59.5);
    }
}
