package com.hospital.ouc.registrationsystem.web;

import com.hospital.ouc.registrationsystem.domain.service.PatientScheduleService;
import com.hospital.ouc.registrationsystem.web.dto.DepartmentDTO;
import com.hospital.ouc.registrationsystem.web.dto.DoctorScheduleForPatientDTO;
import com.hospital.ouc.registrationsystem.web.dto.PatientScheduleQueryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientScheduleController {

    private final PatientScheduleService patientScheduleService;

    public PatientScheduleController(PatientScheduleService patientScheduleService) {
        this.patientScheduleService = patientScheduleService;
    }

    /**
     * 获取所有科室列表
     */
    @GetMapping("/departments")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        return ResponseEntity.ok(patientScheduleService.getAllDepartments());
    }

    /**
     * 患者查询医生排班
     */
    @GetMapping("/schedules")
    public ResponseEntity<List<DoctorScheduleForPatientDTO>> queryDoctorSchedules(
            @RequestParam(required = false) Long departmentId) {

        PatientScheduleQueryDTO queryDTO = new PatientScheduleQueryDTO();
        queryDTO.setDepartmentId(departmentId);

        List<DoctorScheduleForPatientDTO> schedules = patientScheduleService.queryDoctorSchedules(queryDTO);
        return ResponseEntity.ok(schedules);
    }

    /**
     * 根据科室ID获取医生列表和排班
     */
    @GetMapping("/department/{departmentId}/doctors")
    public ResponseEntity<List<DoctorScheduleForPatientDTO>> getDoctorsByDepartment(
            @PathVariable Long departmentId) {
        List<DoctorScheduleForPatientDTO> doctors = patientScheduleService.getDoctorsByDepartment(departmentId);
        return ResponseEntity.ok(doctors);
    }
}