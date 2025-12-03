package com.hospital.ouc.registrationsystem.domain.repository;

import com.hospital.ouc.registrationsystem.domain.entity.DoctorDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorDiseaseRepository extends JpaRepository<DoctorDisease, Long> {
    List<DoctorDisease> findByDoctorProfileId(Long doctorProfileId);
    void deleteByDoctorProfileId(Long doctorProfileId);
}