package com.example.springboot_midterm.service;

import com.example.springboot_midterm.exception.ResourceNotFoundException;
import com.example.springboot_midterm.model.Staff;
import com.example.springboot_midterm.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Staff getStaffById(Long id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with ID: " + id));
    }

    public Optional<Staff> findByUserName(String userName) {
        return staffRepository.findByUserName(userName);
    }

    public Staff saveStaff(Staff staff) {
        return staffRepository.save(staff);
    }

    public void deleteStaff(Long id) {
        if (!staffRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Staff member not found with ID: " + id);
        }
        staffRepository.deleteById(id);
    }

    public Staff authenticate(String username, String password) {
        Staff staff = staffRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password"));
        if (!staff.getPassword().equals(password)) {
            throw new ResourceNotFoundException("Invalid username or password");
        }
        return staff;
    }
}
