package com.hospital.ouc.registrationsystem.web;

import com.hospital.ouc.registrationsystem.domain.service.PatientManagementService;
import com.hospital.ouc.registrationsystem.domain.service.BusinessException;
import com.hospital.ouc.registrationsystem.domain.repository.AppUserRepository;
import com.hospital.ouc.registrationsystem.domain.repository.PatientProfileRepository;
import com.hospital.ouc.registrationsystem.domain.validation.UpdateGroup;
import com.hospital.ouc.registrationsystem.web.dto.ChangePasswordRequest;
import com.hospital.ouc.registrationsystem.web.dto.PatientDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 患者自服务控制器：提供患者查看/修改个人信息及修改密码的接口
 */
@RestController
@RequestMapping("/api/patient/profile")
@RequiredArgsConstructor
public class PatientProfileController {

    private final PatientManagementService patientManagementService;
    private final AppUserRepository appUserRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String SALT = "OucWebDev123"; // 与 AuthService 保持一致

    /** 查询患者信息（通过患者档案ID） */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getProfile(@PathVariable Long id) {
        PatientDTO dto = patientManagementService.getPatientById(id);
        return ResponseEntity.ok(dto);
    }

    /** 根据用户ID查询患者档案（有时登录返回未携带 patientId，前端可用 userId 回退） */
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<PatientDTO> getProfileByUserId(@PathVariable Long userId) {
        var profileOpt = patientProfileRepository.findByUserId(userId);
        if (profileOpt.isEmpty()) throw new BusinessException("患者档案不存在");
        Long profileId = profileOpt.get().getId();
        PatientDTO dto = patientManagementService.getPatientById(profileId);
        return ResponseEntity.ok(dto);
    }

    /** 修改患者信息（部分更新） */
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updateProfile(@PathVariable Long id,
                                                    @Validated(UpdateGroup.class) @RequestBody PatientDTO patientDTO) {
        PatientDTO result = patientManagementService.updatePatient(id, patientDTO);
        return ResponseEntity.ok(result);
    }

    /** 修改密码：直接设置新密码（前端传入明文新密码），保存时采用项目统一的 sha256Hex(plain+SALT) 算法，并使当前会话失效（强制重新登录） */
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
                                               @Valid @RequestBody ChangePasswordRequest req,
                                               HttpServletRequest request) {
        // 兼容处理：id 可能是 patientProfileId，也可能是 userId。优先按 patientProfileId 解析。
        PatientDTO profile;
        try {
            profile = patientManagementService.getPatientById(id);
        } catch (BusinessException ex) {
            // 作为回退，尝试将传入的 id 当作 userId 来查找患者档案
            var profileOpt = patientProfileRepository.findByUserId(id);
            if (profileOpt.isEmpty()) {
                throw ex; // 原始异常继续抛出
            }
            profile = patientManagementService.getPatientById(profileOpt.get().getId());
        }

        Long userId = profile.getUserId();
        var userOpt = appUserRepository.findById(userId);
        if (userOpt.isEmpty()) throw new BusinessException("关联用户不存在");
        var user = userOpt.get();

        // 使用与 AuthService 相同的哈希算法：sha256(plain + SALT) -> hex
        String hashed = sha256Hex(req.getNewPassword() + SALT);
        user.setPassword(hashed);
        appUserRepository.save(user);

        // 使当前会话失效，用户需要重新登录
        try {
            var session = request.getSession(false);
            if (session != null) session.invalidate();
        } catch (Exception ignored) {
        }

        return ResponseEntity.noContent().build();
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 不可用", e);
        }
    }
}
