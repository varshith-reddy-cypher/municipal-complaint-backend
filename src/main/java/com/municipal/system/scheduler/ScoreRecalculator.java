package com.municipal.system.scheduler;

import com.municipal.system.model.Complaint;
import com.municipal.system.repository.ComplaintRepository;
import com.municipal.system.service.PriorityAlgorithmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScoreRecalculator {

    private static final Logger log = LoggerFactory.getLogger(ScoreRecalculator.class);

    private final ComplaintRepository complaintRepository;
    private final PriorityAlgorithmService priorityAlgorithmService;

    public ScoreRecalculator(ComplaintRepository complaintRepository,
                             PriorityAlgorithmService priorityAlgorithmService) {
        this.complaintRepository = complaintRepository;
        this.priorityAlgorithmService = priorityAlgorithmService;
    }

    @Scheduled(fixedRateString = "${prioritization.recalculation-rate-ms:300000}")
    @Transactional
    public void recalculateAllActiveScores() {
        List<Complaint> activeComplaints = complaintRepository.findActiveComplaintsForRecalculation();
        if (activeComplaints.isEmpty()) {
            return;
        }

        log.info("Running dynamic priority score recalculation for {} active complaints...", activeComplaints.size());

        int updatedCount = 0;
        for (Complaint complaint : activeComplaints) {
            double oldScore = complaint.getPriorityScore();
            double newScore = priorityAlgorithmService.calculateScore(complaint);

            if (Math.abs(newScore - oldScore) >= 0.01) {
                complaint.setPriorityScore(newScore);
                complaint.setUpdatedAt(LocalDateTime.now());
                complaintRepository.save(complaint);
                updatedCount++;
            }
        }

        log.info("Priority recalculation complete. Updated {} tickets dynamically.", updatedCount);
    }
}
