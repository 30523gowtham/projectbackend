package com.hospital.service;

import com.hospital.dto.AuthResponse;
import com.hospital.dto.LoginRequest;
import com.hospital.dto.RegisterRequest;
import com.hospital.entity.Patient;
import com.hospital.repository.PatientRepository;
import com.hospital.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    @Autowired
    private PatientRepository repo;

    @Autowired
    private JwtUtil jwt;

    public String register(RegisterRequest req) {
        Patient p = new Patient();
        p.setName(req.getName());
        p.setEmail(req.getEmail());
        p.setPassword(req.getPassword()); // Plain text for now
        p.setAge(req.getAge());
        p.setGender(req.getGender());
        repo.save(p);
        return "Patient registered";
    }
    
    public AuthResponse login(LoginRequest req) {
        Patient p = repo.findByEmail(req.getEmail());

        if (p == null) {
            System.out.println("❌ No patient found with email: " + req.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        String dbPassword = p.getPassword();
        String requestPassword = req.getPassword();

        System.out.println("✅ Patient found: " + p.getEmail());
        System.out.println("🔐 DB password: [" + dbPassword + "]");
        System.out.println("🔐 Request password: [" + requestPassword + "]");

        if (dbPassword != null && requestPassword != null && dbPassword.trim().equals(requestPassword.trim())) {
            String token = jwt.generateToken(p.getEmail());
            System.out.println("✅ Token generated: " + token);
            return new AuthResponse(token, p.getEmail(), "PATIENT");
        }

        System.out.println("❌ Password mismatch");
        throw new RuntimeException("Invalid credentials");
    }

}