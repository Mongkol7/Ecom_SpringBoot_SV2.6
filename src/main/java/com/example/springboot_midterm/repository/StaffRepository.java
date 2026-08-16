package com.example.springboot_midterm.repository;

import com.example.springboot_midterm.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUserName(String userName);
    List<Staff> findByRole(String role);
}
