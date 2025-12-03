package com.hospital.ouc.registrationsystem.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 疾病实体，对应数据库表 disease。
 * 记录疾病的基础信息，并关联所属科室（department）。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "disease")
public class Disease {
    /**
     * 主键ID（自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 疾病名称（非空）
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 疾病代码（唯一，可空）
     */
    @Column(unique = true, length = 50)
    private String code;

    /**
     * 疾病描述（可空）
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 所属科室（非空），多对一关联 department
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}

