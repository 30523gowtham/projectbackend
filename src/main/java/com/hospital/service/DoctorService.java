package com.hospital.service;

import com.hospital.dto.AuthResponse;
import com.hospital.dto.LoginRequest;
import com.hospital.dto.RegisterRequest;
import com.hospital.entity.Doctor;
import com.hospital.repository.DoctorRepository;
import com.hospital.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository repo;

    @Autowired
    private JwtUtil jwt;

    // ✅ Register a new doctor
    public String register(RegisterRequest req) {
        Doctor d = new Doctor();
        d.setName(req.getName());
        d.setEmail(req.getEmail());
        d.setPassword(req.getPassword());
        repo.save(d);
        return "Doctor registered";
    }

    // ✅ Get all doctor objects
    public List<Doctor> getAllDoctors() {
        return repo.findAll();
    }

    // ✅ Login and return token + email + role
    public AuthResponse login(LoginRequest req) {
        Doctor d = repo.findByEmail(req.getEmail());
        if (d != null && d.getPassword().equals(req.getPassword())) {
            String token = jwt.generateToken(d.getEmail());

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setEmail(d.getEmail());
            response.setRole("DOCTOR"); // You can later fetch this from Doctor entity

            return response;
        }
        throw new RuntimeException("Invalid credentials");
    }

    // ✅ Get only doctor names
    public List<String> getAllDoctorNames() {
        return repo.findAll()
                   .stream()
                   .map(Doctor::getName)
                   .collect(Collectors.toList());
    }
}