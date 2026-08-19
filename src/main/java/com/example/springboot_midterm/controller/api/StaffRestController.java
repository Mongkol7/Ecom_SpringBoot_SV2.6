package com.example.springboot_midterm.controller.api;

import com.example.springboot_midterm.model.Staff;
import com.example.springboot_midterm.service.StaffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffRestController {

    private final StaffService staffService;

    public StaffRestController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ResponseEntity<List<Staff>> getAllStaff() {
        List<Staff> staffList = staffService.getAllStaff();
        return ResponseEntity.ok(staffList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable("id") Long id) {
        try {
            Staff staff = staffService.getStaffById(id);
            return ResponseEntity.ok(staff);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Staff> createStaff(@RequestBody Staff staff) {
        Staff saved = staffService.saveStaff(staff);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Staff> updateStaff(@PathVariable("id") Long id, @RequestBody Staff staff) {
        try {
            Staff existing = staffService.getStaffById(id);
            existing.setFullName(staff.getFullName());
            existing.setUserName(staff.getUserName());
            if (staff.getPassword() != null && !staff.getPassword().isEmpty()) {
                existing.setPassword(staff.getPassword());
            }
            existing.setRole(staff.getRole());
            if (staff.getImageUrl() != null) {
                existing.setImageUrl(staff.getImageUrl());
            }
            Staff updated = staffService.saveStaff(existing);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable("id") Long id) {
        try {
            staffService.deleteStaff(id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
