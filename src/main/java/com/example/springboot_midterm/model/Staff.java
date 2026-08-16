package com.example.springboot_midterm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sid")
    private Long sId;

    @Column(name = "username", nullable = false, unique = true)
    private String userName;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role; // ADMIN, STOCK, USER

    @Column(name = "full_name")
    private String fullName;

    public Staff() {}

    public Staff(String userName, String password, String role, String fullName) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }

    public Long getSId() {
        return sId;
    }

    public void setSId(Long sId) {
        this.sId = sId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
