package com.municipal.system.service;

import com.municipal.system.model.*;
import com.municipal.system.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataInitializationService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializationService.class);

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final ComplaintRepository complaintRepository;
    private final StatusLogRepository statusLogRepository;
    private final PriorityAlgorithmService priorityAlgorithmService;

    public DataInitializationService(UserRepository userRepository,
                                   DepartmentRepository departmentRepository,
                                   ComplaintRepository complaintRepository,
                                   StatusLogRepository statusLogRepository,
                                   PriorityAlgorithmService priorityAlgorithmService) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.complaintRepository = complaintRepository;
        this.statusLogRepository = statusLogRepository;
        this.priorityAlgorithmService = priorityAlgorithmService;
    }

    @Override
    public void run(String... args) {
        if (departmentRepository.count() > 0) {
            return;
        }

        log.info("--- Initializing Municipal Complaint System Seed Data ---");

        Department deptRoads = departmentRepository.save(new Department("Roads & Infrastructure", null));
        Department deptWater = departmentRepository.save(new Department("Water Supply & Sewerage", null));
        Department deptElectrical = departmentRepository.save(new Department("Electrical & Street Lighting", null));
        Department deptSanitation = departmentRepository.save(new Department("Sanitation & Waste Management", null));

        User citizen1 = userRepository.save(new User("Aarav Sharma", "aarav@example.com", Role.CITIZEN, "ZONE-NORTH"));
        User citizen2 = userRepository.save(new User("Priya Patel", "priya@example.com", Role.CITIZEN, "ZONE-SOUTH"));
        User citizen3 = userRepository.save(new User("Rohan Iyer", "rohan@example.com", Role.CITIZEN, "ZONE-CENTRAL"));

        User officerNorth = userRepository.save(new User("Officer Vikram Singh", "vikram.officer@municipal.gov", Role.OFFICER, "ZONE-NORTH"));
        User officerSouth = userRepository.save(new User("Officer Ananya Roy", "ananya.officer@municipal.gov", Role.OFFICER, "ZONE-SOUTH"));
        User officerCentral = userRepository.save(new User("Officer Suresh Menon", "suresh.officer@municipal.gov", Role.OFFICER, "ZONE-CENTRAL"));

        userRepository.save(new User("Municipal Admin", "admin@municipal.gov", Role.ADMIN, "ZONE-CENTRAL"));

        deptRoads.setHeadOfficerId(officerNorth.getUserId());
        deptWater.setHeadOfficerId(officerSouth.getUserId());
        deptElectrical.setHeadOfficerId(officerCentral.getUserId());
        departmentRepository.save(deptRoads);
        departmentRepository.save(deptWater);
        departmentRepository.save(deptElectrical);

        // 1. Critical Water Main Burst (+50 boost)
        Complaint waterBurst = new Complaint();
        waterBurst.setTitle("Major Water Pipeline Burst Flooding Main Road");
        waterBurst.setDescription("Sub-surface water pipe ruptured, flooding traffic intersection and threatening water contamination.");
        waterBurst.setCitizen(citizen1);
        waterBurst.setDepartment(deptWater);
        waterBurst.setZoneId("ZONE-NORTH");
        waterBurst.setLatitude(12.9716);
        waterBurst.setLongitude(77.5946);
        waterBurst.setPhotoUrl("https://images.unsplash.com/photo-1541888946425-d0fbb1861593");
        waterBurst.setSeverityLevel(5);
        waterBurst.setAffectedCount(45);
        waterBurst.setIsCriticalHazard(true);
        waterBurst.setStatus(ComplaintStatus.VERIFIED);
        waterBurst.setCreatedAt(LocalDateTime.now().minusHours(4));
        waterBurst.setUpdatedAt(LocalDateTime.now().minusHours(2));
        waterBurst.setPriorityScore(priorityAlgorithmService.calculateScore(waterBurst));
        Complaint savedWaterBurst = complaintRepository.save(waterBurst);

        statusLogRepository.save(new StatusLog(savedWaterBurst, ComplaintStatus.REPORTED, "Initial complaint filed by citizen", waterBurst.getPhotoUrl(), citizen1));
        statusLogRepository.save(new StatusLog(savedWaterBurst, ComplaintStatus.VERIFIED, "Verified by Municipal field inspector as urgent critical hazard", null, officerNorth));

        // 2. Dangerous Pothole
        Complaint pothole = new Complaint();
        pothole.setTitle("Dangerous Deep Pothole Near Primary School");
        pothole.setDescription("2-foot wide pothole causing two-wheeler accidents during evening peak hours.");
        pothole.setCitizen(citizen2);
        pothole.setDepartment(deptRoads);
        pothole.setZoneId("ZONE-SOUTH");
        pothole.setLatitude(12.9352);
        pothole.setLongitude(77.6245);
        pothole.setPhotoUrl("https://images.unsplash.com/photo-1515162816999-a0c47dc192f7");
        pothole.setSeverityLevel(4);
        pothole.setAffectedCount(22);
        pothole.setIsCriticalHazard(true);
        pothole.setStatus(ComplaintStatus.REPORTED);
        pothole.setCreatedAt(LocalDateTime.now().minusHours(12));
        pothole.setUpdatedAt(LocalDateTime.now().minusHours(12));
        pothole.setPriorityScore(priorityAlgorithmService.calculateScore(pothole));
        Complaint savedPothole = complaintRepository.save(pothole);
        statusLogRepository.save(new StatusLog(savedPothole, ComplaintStatus.REPORTED, "Filed by citizen", pothole.getPhotoUrl(), citizen2));

        // 3. Streetlight Flickering
        Complaint streetlight = new Complaint();
        streetlight.setTitle("Streetlight 42 Flickering at Night");
        streetlight.setDescription("Light bulb in lane 3 flickers, causing minor inconvenience.");
        streetlight.setCitizen(citizen3);
        streetlight.setDepartment(deptElectrical);
        streetlight.setZoneId("ZONE-CENTRAL");
        streetlight.setLatitude(12.9780);
        streetlight.setLongitude(77.6408);
        streetlight.setPhotoUrl("https://images.unsplash.com/photo-1509198397868-475647b2a1e5");
        streetlight.setSeverityLevel(2);
        streetlight.setAffectedCount(4);
        streetlight.setIsCriticalHazard(false);
        streetlight.setStatus(ComplaintStatus.REPORTED);
        streetlight.setCreatedAt(LocalDateTime.now().minusHours(2));
        streetlight.setUpdatedAt(LocalDateTime.now().minusHours(2));
        streetlight.setPriorityScore(priorityAlgorithmService.calculateScore(streetlight));
        Complaint savedStreetlight = complaintRepository.save(streetlight);
        statusLogRepository.save(new StatusLog(savedStreetlight, ComplaintStatus.REPORTED, "Filed by citizen", streetlight.getPhotoUrl(), citizen3));

        // 4. Garbage Cleaned
        Complaint garbage = new Complaint();
        garbage.setTitle("Garbage Bin Overflowing at Community Park");
        garbage.setDescription("Waste bin overflowed after weekend festival.");
        garbage.setCitizen(citizen1);
        garbage.setDepartment(deptSanitation);
        garbage.setZoneId("ZONE-NORTH");
        garbage.setLatitude(12.9800);
        garbage.setLongitude(77.5800);
        garbage.setPhotoUrl("https://images.unsplash.com/photo-1532996122724-e3c354a0b15b");
        garbage.setSeverityLevel(3);
        garbage.setAffectedCount(15);
        garbage.setIsCriticalHazard(false);
        garbage.setStatus(ComplaintStatus.RESOLVED);
        garbage.setCreatedAt(LocalDateTime.now().minusHours(48));
        garbage.setUpdatedAt(LocalDateTime.now().minusHours(6));
        garbage.setPriorityScore(0.0);
        Complaint savedGarbage = complaintRepository.save(garbage);
        statusLogRepository.save(new StatusLog(savedGarbage, ComplaintStatus.REPORTED, "Filed by citizen", garbage.getPhotoUrl(), citizen1));
        statusLogRepository.save(new StatusLog(savedGarbage, ComplaintStatus.RESOLVED, "Cleaned and sanitized by morning sanitation crew", null, officerNorth));

        log.info("--- Successfully Seeded Departments, Users, and Sample Prioritized Complaints ---");
    }
}
