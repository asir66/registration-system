package com.hospital.ouc.registrationsystem.web.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PatientRegistrationInfoDTO {
    private Long id;
    private Long patientProfileId;
    private Long doctorProfileId;
    private String doctorId; // doctor工号
    private String doctorName;
    private String doctorTitle;

    private Long diseaseId;
    private String diseaseName;
    private Long departmentId;
    private String departmentName;

    private Integer weekday; // 1-5
    private String timeslot; // AM1..PM4
    private String status;
    private LocalDateTime registrationTime;
}

