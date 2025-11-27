package com.hospital.repository;

import com.hospital.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorEmail(String doctorEmail);
    List<Appointment> findByPatientEmail(String patientEmail);
    boolean existsByDoctorEmailAndDateAndTime(String doctorEmail, String date, String time);
}