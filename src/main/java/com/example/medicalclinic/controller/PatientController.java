package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.mapper.PatientMapper;
import com.example.medicalclinic.model.ChangePasswordCommand;
import com.example.medicalclinic.model.entity.Patient;
import com.example.medicalclinic.model.dto.PatientDTO;
import com.example.medicalclinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @GetMapping
    public PageableContentDTO<PatientDTO> getPatients(Pageable pageable) {
        return patientService.getAllPatients(pageable);
    }

    @GetMapping("/{email}")
    public PatientDTO getPatientByEmail(@PathVariable("email") String email) {
        return patientService.getPatientByEmail(email);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void removePatient(@PathVariable("email") String email) {
        patientService.removePatientByEmail(email);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PatientDTO addPatient(@RequestBody Patient patient) {
        return patientService.addPatient(patient);
    }

    @PutMapping("/{email}")
    public PatientDTO editPatient(@PathVariable("email") String email, @RequestBody Patient patient) {
        return patientService.editPatientByEmail(email, patient);
    }

    @PatchMapping("/{email}/password")
    public Patient editPatientPassword(@PathVariable String email, @RequestBody ChangePasswordCommand request) {
        return patientService.changePassword(email, request.password());
    }
}
