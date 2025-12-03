package com.hospital.ouc.registrationsystem.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 医生-疾病关联实体，对应数据库表 doctor_disease。
 * 用于表示某位医生可诊治的具体疾病（多对多中间表）。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "doctor_disease",
       uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_profile_id", "disease_id"}))
public class DoctorDisease {
    /**
     * 主键ID（自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 医生档案（非空）
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_profile_id", nullable = false)
    private DoctorProfile doctorProfile;

    /**
     * 疾病（非空）
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;
}

