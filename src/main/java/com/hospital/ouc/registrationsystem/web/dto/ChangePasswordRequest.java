package com.hospital.ouc.registrationsystem.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}

