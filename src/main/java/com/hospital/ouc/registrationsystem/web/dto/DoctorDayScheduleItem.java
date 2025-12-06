package com.hospital.ouc.registrationsystem.web.dto;

import lombok.Data;

@Data
public class DoctorDayScheduleItem {
    private String timeslot; // AM1..PM4
    private Long patientId;
    private String patientName;
    private String patientIdCard;
    private String patientPhone;
    private Integer patientAge;
    private String patientGender; // male/female

    // 新增：是否已取消（用于医生界面展示）
    private Boolean canceled; // true -> 已取消, false/null -> 未取消/待接诊

    // 新增：挂号状态原始字符串，便于前端兼容判断（PENDING/PAID/CANCELLED/COMPLETED）
    private String status;
}
