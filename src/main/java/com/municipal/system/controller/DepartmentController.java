package com.municipal.system.controller;

import com.municipal.system.dto.ApiResponse;
import com.municipal.system.model.Department;
import com.municipal.system.repository.DepartmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "Departments", description = "Municipal departments (Roads, Water, Electrical, Sanitation)")
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    @Operation(summary = "List all municipal departments")
    public ResponseEntity<ApiResponse<List<Department>>> getAllDepartments() {
        return ResponseEntity.ok(ApiResponse.success(departmentRepository.findAll()));
    }

    @PostMapping
    @Operation(summary = "Create department")
    public ResponseEntity<ApiResponse<Department>> createDepartment(@RequestBody Department dept) {
        Department created = departmentRepository.save(dept);
        return ResponseEntity.ok(ApiResponse.success("Department created successfully", created));
    }
}
