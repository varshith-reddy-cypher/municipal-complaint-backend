package com.municipal.system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long deptId;

    @Column(name = "dept_name", nullable = false, unique = true, length = 100)
    private String deptName;

    @Column(name = "head_officer_id")
    private Long headOfficerId;

    public Department() {
    }

    public Department(String deptName, Long headOfficerId) {
        this.deptName = deptName;
        this.headOfficerId = headOfficerId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Long getHeadOfficerId() {
        return headOfficerId;
    }

    public void setHeadOfficerId(Long headOfficerId) {
        this.headOfficerId = headOfficerId;
    }
}
