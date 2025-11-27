package com.hospital.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {
    private String doctorEmail;
    private String appointmentDate;
    private String appointmentTime;
    private String reason;
}