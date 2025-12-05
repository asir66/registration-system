package com.hospital.ouc.registrationsystem.web;

import com.hospital.ouc.registrationsystem.domain.service.PatientRegistrationService;
import com.hospital.ouc.registrationsystem.web.dto.RegistrationRequestDTO;
import com.hospital.ouc.registrationsystem.web.dto.RegistrationResponseDTO;
import com.hospital.ouc.registrationsystem.web.dto.PatientRegistrationInfoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/registration")
@CrossOrigin(origins = "*")
public class PatientRegistrationController {

    private final PatientRegistrationService registrationService;

    public PatientRegistrationController(PatientRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<RegistrationResponseDTO> register(@RequestBody RegistrationRequestDTO request) {
        RegistrationResponseDTO resp = registrationService.register(request);
        return ResponseEntity.ok(resp);
    }

    // 新增：根据患者档案ID查询挂号记录
    @GetMapping("/patient/{patientProfileId}")
    public ResponseEntity<List<PatientRegistrationInfoDTO>> listByPatient(@PathVariable Long patientProfileId) {
        List<PatientRegistrationInfoDTO> list = registrationService.listRegistrationsByPatient(patientProfileId);
        return ResponseEntity.ok(list);
    }

    // 新增：取消挂号
    @DeleteMapping("/{registrationId}")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long registrationId,
                                                   @RequestParam Long patientProfileId) {
        registrationService.cancelRegistration(registrationId, patientProfileId);
        return ResponseEntity.noContent().build();
    }
}
