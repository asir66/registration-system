package com.hospital.ouc.registrationsystem.web.dto;

import lombok.Data;

import java.util.List;

@Data
public class DoctorForPatientDTO {
    private Long id; // doctor profile id
    private String doctorId; // 工号
    private String name;
    private Integer age;
    private String gender;
    private String title;
    private Long departmentId;
    private String departmentName;
    private List<DiseaseDTO> diseases;
}

