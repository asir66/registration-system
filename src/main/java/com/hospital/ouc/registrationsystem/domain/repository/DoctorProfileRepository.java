package com.hospital.ouc.registrationsystem.domain.repository;

import com.hospital.ouc.registrationsystem.domain.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorProfileRepository extends JpaRepository<DoctorProfile, Long> {
    Optional<DoctorProfile> findByDoctorId(String doctorId);
    List<DoctorProfile> findByIsActive(boolean isActive);
    List<DoctorProfile> findByDepartmentIdAndIsActive(Long departmentId, boolean isActive);

    // 添加查询所有医生的方法（不区分is_active状态）
    List<DoctorProfile> findAll();
}