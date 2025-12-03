package com.hospital.ouc.registrationsystem.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.List;

@Data
public class DoctorDTO {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "医生工号不能为空")
    private String doctorId;

    @NotBlank(message = "医生姓名不能为空")
    private String name;

    private Integer age;

    @Pattern(regexp = "^(male|female)$", message = "性别必须为male或female")
    private String gender;

    private String title;

    @NotNull(message = "科室ID不能为空")
    private Long departmentId;

    private String departmentName;
    private boolean isActive;
    private List<Long> diseaseIds;
}
