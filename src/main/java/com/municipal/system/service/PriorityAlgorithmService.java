package com.municipal.system.service;

import com.municipal.system.model.Complaint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PriorityAlgorithmService {

    @Value("${prioritization.weights.w1-severity:0.5}")
    private double w1Severity;

    @Value("${prioritization.weights.w2-affected:0.3}")
    private double w2Affected;

    @Value("${prioritization.weights.w3-age:0.2}")
    private double w3Age;

    @Value("${prioritization.hazard-boost:50.0}")
    private double hazardBoost;

    public double calculateScore(Complaint complaint) {
        if (complaint == null) {
            return 0.0;
        }

        int baseSeverity = complaint.getSeverityLevel() != null ? complaint.getSeverityLevel() : 1;
        int affectedCount = complaint.getAffectedCount() != null ? complaint.getAffectedCount() : 1;

        double ageInHours = calculateAgeInHours(complaint.getCreatedAt());

        double baseScore = (baseSeverity * w1Severity) +
                           (affectedCount * w2Affected) +
                           (ageInHours * w3Age);

        if (Boolean.TRUE.equals(complaint.getIsCriticalHazard())) {
            baseScore += hazardBoost;
        }

        return Math.round(baseScore * 100.0) / 100.0;
    }

    public double calculateAgeInHours(LocalDateTime createdAt) {
        if (createdAt == null) {
            return 0.0;
        }
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long seconds = Math.max(0, duration.getSeconds());
        return seconds / 3600.0;
    }

    public double getW1Severity() {
        return w1Severity;
    }

    public double getW2Affected() {
        return w2Affected;
    }

    public double getW3Age() {
        return w3Age;
    }

    public double getHazardBoost() {
        return hazardBoost;
    }
}
