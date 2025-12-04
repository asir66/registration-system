package com.hospital.ouc.registrationsystem.domain.repository;

import com.hospital.ouc.registrationsystem.domain.entity.DoctorDepartmentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorDepartmentScheduleRepository extends JpaRepository<DoctorDepartmentSchedule, Long> {
    /**
     * 根据医生工号查询一周内的全部排班记录。
     */
    List<DoctorDepartmentSchedule> findByDoctorProfile_DoctorId(String doctorId);
}
