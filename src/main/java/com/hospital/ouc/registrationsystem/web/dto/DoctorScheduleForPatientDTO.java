package com.hospital.ouc.registrationsystem.web.dto;

import lombok.Data;
import com.hospital.ouc.registrationsystem.domain.enums.TimeSlot;

@Data
public class DoctorScheduleForPatientDTO {
    private Long scheduleId;
    private Long doctorId;
    private String doctorName;
    private String doctorTitle;
    private String doctorGender;
    private Integer doctorAge;
    private Long departmentId;
    private String departmentName;
    private Integer weekday;
    private TimeSlot timeslot;

    private Integer currentPatients;
    private Integer maxPatients = 10;
    private Boolean available;
}