package com.municipal.system.controller;

import com.municipal.system.dto.ApiResponse;
import com.municipal.system.model.Role;
import com.municipal.system.model.User;
import com.municipal.system.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users Management", description = "Citizens, Field Officers, and Administrators")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @Operation(summary = "List all users or filter by role")
    public ResponseEntity<ApiResponse<List<User>>> getUsers(@RequestParam(required = false) Role role) {
        List<User> users = (role != null) ? userRepository.findByRole(role) : userRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/officers")
    @Operation(summary = "List all field officers")
    public ResponseEntity<ApiResponse<List<User>>> getOfficers(@RequestParam(required = false) String zoneId) {
        List<User> officers = (zoneId != null)
                ? userRepository.findByRoleAndZoneId(Role.OFFICER, zoneId)
                : userRepository.findByRole(Role.OFFICER);
        return ResponseEntity.ok(ApiResponse.success(officers));
    }

    @PostMapping
    @Operation(summary = "Register citizen or officer")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        User created = userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", created));
    }
}
