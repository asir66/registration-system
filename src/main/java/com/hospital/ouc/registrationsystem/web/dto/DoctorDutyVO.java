package com.hospital.ouc.registrationsystem.web.dto;

import lombok.Data;

@Data
public class DoctorDutyVO {
    private Long id;
    private String departmentName;
    private Long departmentId;
    private Long doctorId;
    private String doctorName;
    private String doctorTitle;
    private Integer weekendType;
    private String dutyTimeslot;
}

