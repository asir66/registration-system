package com.hospital.ouc.registrationsystem.domain.service;

import com.hospital.ouc.registrationsystem.domain.entity.*;
import com.hospital.ouc.registrationsystem.domain.repository.*;
import com.hospital.ouc.registrationsystem.web.dto.DepartmentDTO;
import com.hospital.ouc.registrationsystem.web.dto.DoctorScheduleForPatientDTO;
import com.hospital.ouc.registrationsystem.web.dto.PatientScheduleQueryDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PatientScheduleService {

    private final DoctorDepartmentScheduleRepository scheduleRepository;
    private final PatientDoctorRegistrationRepository registrationRepository;
    private final DoctorProfileRepository doctorProfileRepository;

    public PatientScheduleService(
            DoctorDepartmentScheduleRepository scheduleRepository,
            PatientDoctorRegistrationRepository registrationRepository,
            DoctorProfileRepository doctorProfileRepository) {
        this.scheduleRepository = scheduleRepository;
        this.registrationRepository = registrationRepository;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    /**
     * 获取所有科室列表
     */
    public List<DepartmentDTO> getAllDepartments() {
        Set<Department> departments = new HashSet<>();

        // 从排班表中获取所有不重复的科室
        List<DoctorDepartmentSchedule> allSchedules = scheduleRepository.findAll();
        for (DoctorDepartmentSchedule schedule : allSchedules) {
            if (schedule.getDepartment() != null) {
                departments.add(schedule.getDepartment());
            }
        }

        // 从医生表中获取所有不重复的科室（作为补充）
        List<DoctorProfile> allDoctors = doctorProfileRepository.findAll();
        for (DoctorProfile doctor : allDoctors) {
            if (doctor.getDepartment() != null) {
                departments.add(doctor.getDepartment());
            }
        }

        return departments.stream()
                .map(dept -> {
                    DepartmentDTO dto = new DepartmentDTO();
                    dto.setId(dept.getId());
                    dto.setDepartmentName(dept.getDepartmentName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 患者查询医生排班（基础版本，只支持按科室筛选）
     */
    public List<DoctorScheduleForPatientDTO> queryDoctorSchedules(PatientScheduleQueryDTO queryDTO) {
        List<DoctorDepartmentSchedule> schedules;

        if (queryDTO.getDepartmentId() != null) {
            // 按科室筛选
            schedules = scheduleRepository.findByDepartmentId(queryDTO.getDepartmentId());
        } else {
            // 查看所有排班
            schedules = scheduleRepository.findAll();
        }

        // 转换为DTO
        List<DoctorScheduleForPatientDTO> dtos = new ArrayList<>();
        for (DoctorDepartmentSchedule schedule : schedules) {
            DoctorScheduleForPatientDTO dto = new DoctorScheduleForPatientDTO();
            DoctorProfile doctor = schedule.getDoctorProfile();
            Department department = schedule.getDepartment();

            dto.setScheduleId(schedule.getId());
            dto.setDoctorId(doctor.getId());
            dto.setDoctorName(doctor.getName());
            dto.setDoctorTitle(doctor.getTitle());
            dto.setDoctorAge(doctor.getAge());
            dto.setDoctorGender(doctor.getGender().name());
            dto.setDepartmentId(department.getId());
            dto.setDepartmentName(department.getDepartmentName());
            dto.setWeekday(schedule.getWeekday());
            dto.setTimeslot(schedule.getTimeslot());

            // 简单统计（后续完善）
            dto.setCurrentPatients(0);
            dto.setAvailable(true);

            dtos.add(dto);
        }

        return dtos;
    }

    /**
     * 根据科室ID获取医生列表
     */
    public List<DoctorScheduleForPatientDTO> getDoctorsByDepartment(Long departmentId) {
        // 先获取该科室下的所有医生
        List<DoctorProfile> doctors = doctorProfileRepository.findByDepartmentId(departmentId);

        // 获取每个医生的排班信息
        List<DoctorScheduleForPatientDTO> result = new ArrayList<>();
        for (DoctorProfile doctor : doctors) {
            List<DoctorDepartmentSchedule> doctorSchedules = scheduleRepository.findByDoctorProfile_Id(doctor.getId());
            for (DoctorDepartmentSchedule schedule : doctorSchedules) {
                DoctorScheduleForPatientDTO dto = new DoctorScheduleForPatientDTO();
                Department department = schedule.getDepartment();

                dto.setScheduleId(schedule.getId());
                dto.setDoctorId(doctor.getId());
                dto.setDoctorName(doctor.getName());
                dto.setDoctorTitle(doctor.getTitle());
                dto.setDoctorAge(doctor.getAge());
                dto.setDoctorGender(doctor.getGender().name());
                dto.setDepartmentId(department.getId());
                dto.setDepartmentName(department.getDepartmentName());
                dto.setWeekday(schedule.getWeekday());
                dto.setTimeslot(schedule.getTimeslot());

                dto.setCurrentPatients(0);
                dto.setAvailable(true);

                result.add(dto);
            }
        }

        return result;
    }
}