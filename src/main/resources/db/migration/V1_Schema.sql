-- V1_Schema.sql: Municipal Complaint Prioritization System (MySQL Dialect)
-- Conforms to Section 7 (DATABASE ER DIAGRAM ARCHITECTURE)

CREATE DATABASE IF NOT EXISTS municipal_db;
USE municipal_db;

-- 1. Departments Table (Roads, Water, Electrical, Sanitation)
CREATE TABLE IF NOT EXISTS department (
    dept_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL UNIQUE,
    head_officer_id BIGINT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Users Table (Citizens, Field Officers, Administrators)
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL, -- CITIZEN, OFFICER, ADMIN
    zone_id VARCHAR(50) NOT NULL, -- e.g. ZONE-NORTH, ZONE-SOUTH, ZONE-CENTRAL
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Complaints Table
CREATE TABLE IF NOT EXISTS complaints (
    complaint_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    citizen_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    zone_id VARCHAR(50) NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    photo_url VARCHAR(500),
    severity_level INT NOT NULL,
    affected_count INT NOT NULL DEFAULT 1,
    priority_score DOUBLE NOT NULL DEFAULT 0.0,
    is_critical_hazard BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(50) NOT NULL DEFAULT 'REPORTED', -- REPORTED, VERIFIED, ASSIGNED, IN_PROGRESS, RESOLVED
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_complaints_citizen FOREIGN KEY (citizen_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_complaints_department FOREIGN KEY (department_id) REFERENCES department(dept_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Assignments Table (Department & Field Officer routing)
CREATE TABLE IF NOT EXISTS assignments (
    assignment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT NOT NULL,
    officer_id BIGINT NOT NULL,
    assigned_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    CONSTRAINT fk_assignments_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id) ON DELETE CASCADE,
    CONSTRAINT fk_assignments_officer FOREIGN KEY (officer_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Status Logs Table (Audit trail & Citizen timeline feedback)
CREATE TABLE IF NOT EXISTS status_logs (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    remarks TEXT,
    photo_url VARCHAR(500),
    updated_by BIGINT,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_status_logs_complaint FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id) ON DELETE CASCADE,
    CONSTRAINT fk_status_logs_user FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Indices for rapid querying & dynamic priority queue sorting
CREATE INDEX idx_complaints_priority ON complaints(priority_score DESC);
CREATE INDEX idx_complaints_status ON complaints(status);
CREATE INDEX idx_complaints_zone ON complaints(zone_id);
CREATE INDEX idx_complaints_dept ON complaints(department_id);
