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
}
