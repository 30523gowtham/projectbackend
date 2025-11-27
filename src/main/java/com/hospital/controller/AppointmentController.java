package com.hospital.controller;

import com.hospital.dto.AppointmentRequest;
import com.hospital.dto.AppointmentResponse;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.repository.DoctorRepository;
import com.hospital.service.AppointmentService;
import com.hospital.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService service;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private DoctorRepository doctorRepo;

    // ✅ Book appointment using JWT-authenticated patient email
    @PostMapping("/book")
    public ResponseEntity<String> book(@RequestBody AppointmentRequest req, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization").substring(7);
            String patientEmail = jwtUtil.extractUsername(token);

            String result = service.book(req, patientEmail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to book appointment: " + e.getMessage());
        }
    }

    // ✅ Get raw appointments for a doctor
    @GetMapping("/doctor/{email}")
    public ResponseEntity<List<Appointment>> getDoctorAppointments(@PathVariable String email) {
        try {
            List<Appointment> appointments = service.getAppointmentsForDoctor(email);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ✅ Get enriched appointments for a patient
    @GetMapping("/patient/{email}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsForPatient(@PathVariable String email) {
        try {
            List<Appointment> appointments = service.getAppointmentsForPatient(email);
            List<Doctor> doctors = doctorRepo.findAll();

            Map<String, Doctor> doctorMap = doctors.stream()
                    .collect(Collectors.toMap(Doctor::getEmail, d -> d));

            List<AppointmentResponse> responses = appointments.stream()
                    .map(a -> {
                        Doctor doc = doctorMap.get(a.getDoctorEmail());
                        return new AppointmentResponse(
                                doc != null ? doc.getName() : a.getDoctorEmail(),
                                doc != null ? doc.getSpecialization() : "—",
                                a.getDate(),
                                a.getTime(),
                                a.getReason()
                        );
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}