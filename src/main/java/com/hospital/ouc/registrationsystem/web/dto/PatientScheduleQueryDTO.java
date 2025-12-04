package com.hospital.ouc.registrationsystem.web.dto;

import lombok.Data;

@Data
public class PatientScheduleQueryDTO {
    private Long departmentId;
    private Integer weekday;
    private String timeslot;
}