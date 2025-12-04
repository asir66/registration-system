// src/main/java/com/hospital/ouc/registrationsystem/web/dto/PatientDTO.java
package com.hospital.ouc.registrationsystem.web.dto;

import com.hospital.ouc.registrationsystem.domain.enums.Gender;
import com.hospital.ouc.registrationsystem.domain.validation.AddGroup;
import com.hospital.ouc.registrationsystem.domain.validation.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PatientDTO {
    private Long id; // 患者档案ID（无需传）
    private Long userId; // 关联用户ID（无需传）
    private String username; // 登录用户名

    // 新增：必填+格式校验；修改：传值则校验格式，不传不校验
    @NotBlank(message = "身份证号不能为空", groups = AddGroup.class)
    @Pattern(regexp = "^[1-9]\\d{17}$", message = "身份证号格式错误", groups = {AddGroup.class, UpdateGroup.class})
    private String idCard;

    // 新增：必填；修改：传值则改，不传不校验（即使传空也不校验，仅新增必填）
    @NotBlank(message = "姓名不能为空", groups = AddGroup.class)
    private String name;

    // 新增：必填+格式校验；修改：传值则校验格式，不传不校验
    @NotBlank(message = "手机号不能为空", groups = AddGroup.class)
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误", groups = {AddGroup.class, UpdateGroup.class})
    private String phoneNumber;

    private Integer age; // 无校验，传值即改，不传保留

    // 新增：必填；修改：传值则改，不传不校验
    @NotNull(message = "性别不能为空", groups = AddGroup.class)
    private Gender gender; // male/female

    private Boolean isActive; // 是否激活（仅后台逻辑修改，前端无需传）
}