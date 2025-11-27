package com.hospital.service;

import com.hospital.dto.AppointmentRequest;
import com.hospital.entity.Appointment;
import com.hospital.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository repo;

    public String book(AppointmentRequest req, String patientEmail) {
        if (patientEmail == null || patientEmail.trim().isEmpty() ||
            req.getDoctorEmail() == null || req.getDoctorEmail().trim().isEmpty() ||
            req.getAppointmentDate() == null || req.getAppointmentDate().trim().isEmpty() ||
            req.getAppointmentTime() == null || req.getAppointmentTime().trim().isEmpty() ||
            req.getReason() == null || req.getReason().trim().isEmpty()) {
            return "Invalid appointment request: missing or empty fields";
        }

        boolean exists = repo.existsByDoctorEmailAndDateAndTime(
            req.getDoctorEmail(), req.getAppointmentDate(), req.getAppointmentTime()
        );

        if (exists) {
            return "Doctor already has an appointment at this time";
        }

        Appointment a = new Appointment();
        a.setPatientEmail(patientEmail);
        a.setDoctorEmail(req.getDoctorEmail());
        a.setDate(req.getAppointmentDate());
        a.setTime(req.getAppointmentTime());
        a.setReason(req.getReason());

        repo.save(a);
        return "Appointment booked successfully";
    }

    public List<Appointment> getAppointmentsForDoctor(String doctorEmail) {
        return repo.findByDoctorEmail(doctorEmail);
    }

    public List<Appointment> getAppointmentsForPatient(String patientEmail) {
        return repo.findByPatientEmail(patientEmail);
    }
}