# Municipal Complaint Prioritization System
### Smart Civic Issue Management & Priority Scoring Engine
**Automated Severity Assessment • Dynamic Priority Queue • Officer Routing**

---

## 1. Project Overview & Abstract
The **Municipal Complaint Prioritization System** is an advanced backend-driven civic management application built with **Java 17**, **Spring Boot 3**, and **MySQL 8**.

Unlike standard static issue tracking systems that process complaints on a simple First-Come-First-Served (FIFO) basis, this application dynamically calculates issue priority using a **multi-factor weighted scoring algorithm** taking into account:
1. Base Severity Level (1-5)
2. Population Impact / Affected Citizen Count (including upvotes)
3. Ticket Age in Hours (SLA aging multiplier)
4. Critical Safety Hazard Override (+50 boost for emergencies like gas/water main bursts or fallen power cables)

It automatically routes tasks to assigned field officers based on municipal department and geographical zone, providing real-time workflow tracking from submission to resolution.

---

## 2. Directory Layout (Spring Boot)
As specified in the architectural design:
```text
backend
├── src/main/java/com/municipal/system
│   ├── MunicipalComplaintSystemApplication.java
│   ├── config
│   │   ├── GlobalExceptionHandler.java
│   │   └── OpenApiConfig.java
│   ├── controller
│   │   ├── AnalyticsController.java
│   │   ├── ComplaintController.java
│   │   ├── DepartmentController.java
│   │   └── UserController.java
│   ├── dto
│   │   ├── ApiResponse.java
│   │   ├── AssignOfficerRequest.java
│   │   ├── ComplaintResponse.java
│   │   ├── CreateComplaintRequest.java
│   │   ├── StatusLogResponse.java
│   │   └── UpdateStatusRequest.java
│   ├── model
│   │   ├── Assignment.java
│   │   ├── Complaint.java
│   │   ├── ComplaintStatus.java
│   │   ├── Department.java
│   │   ├── Role.java
│   │   ├── StatusLog.java
│   │   └── User.java
│   ├── repository
│   │   ├── AssignmentRepository.java
│   │   ├── ComplaintRepository.java
│   │   ├── DepartmentRepository.java
│   │   ├── StatusLogRepository.java
│   │   └── UserRepository.java
│   ├── scheduler
│   │   └── ScoreRecalculator.java
│   └── service
│       ├── AnalyticsService.java
│       ├── ComplaintService.java
│       ├── DataInitializationService.java
│       └── PriorityAlgorithmService.java
├── src/main/resources
│   ├── application.yml
│   └── db/migration/V1_Schema.sql
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── postman_collection.json
```

---

## 3. Priority Scoring Algorithm

### Mathematical Model
$$P = (\text{Base Severity} \times W_1) + (\text{Affected People} \times W_2) + (\text{Age in Hours} \times W_3)$$

Where:
* $W_1 = 0.5$ (Base Severity Weight, level 1 to 5)
* $W_2 = 0.3$ (Affected People / Upvote count)
* $W_3 = 0.2$ (Ticket Age in Hours)
* **Critical Hazard Boost**: If flagged as an imminent danger or hazard (e.g. fallen electric wire, major water pipe rupture), an instant $+50.0$ boost is applied:
$$P_{\text{final}} = P + 50.0$$

---

## 4. REST API Specifications

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/complaints` | Submit ticket with GPS, photo, severity, hazard flag |
| `GET` | `/api/complaints/priority` | Fetch dynamically sorted priority queue |
| `PUT` | `/api/complaints/{id}/assign` | Assign ticket to specific field officer |
| `POST`| `/api/complaints/{id}/auto-dispatch` | Smart auto-dispatch to active zone officer |
| `PATCH`| `/api/complaints/{id}/status` | Update resolution stage (with remarks & proof photo) |
| `POST`| `/api/complaints/{id}/upvote` | Citizen upvote (increases affected count & priority) |
| `GET` | `/api/complaints/{id}` | Get complaint details |
| `GET` | `/api/complaints/{id}/timeline` | Fetch audit logs and timeline |
| `GET` | `/api/reports/analytics` | Zone stats (total, resolved, avg score) |
| `GET` | `/api/reports/summary` | Turnaround metrics & system health |
| `GET` | `/api/departments` | List municipal departments |
| `GET` | `/api/users` | List citizens and officers |

---

## 5. How to Run with MySQL

Database credentials are provided through environment variables; do not commit real credentials.

To start the database and backend with Docker Compose, create a local `.env` file with
`MYSQL_ROOT_PASSWORD` and `MYSQL_APP_PASSWORD` set to strong, unique values, then run:

```bash
docker compose up --build
```

To run the backend directly, set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`,
and `SPRING_DATASOURCE_PASSWORD` in your environment before running `mvn spring-boot:run`.

- **Swagger UI Interactive API Docs**: `http://localhost:8080/swagger-ui.html`
