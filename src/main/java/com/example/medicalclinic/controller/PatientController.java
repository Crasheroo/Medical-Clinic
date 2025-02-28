package com.example.medicalclinic.controller;

import com.example.medicalclinic.mapper.PatientMapper;
import com.example.medicalclinic.model.ChangePasswordRequest;
import com.example.medicalclinic.model.Patient;
import com.example.medicalclinic.model.PatientDTO;
import com.example.medicalclinic.model.ResponseMessage;
import com.example.medicalclinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;
    private final PatientMapper patientMapper = PatientMapper.INSTANCE;

    @GetMapping
    public List<PatientDTO> getPatientsDTO() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{email}")
    public PatientDTO getPatientByEmail(@PathVariable("email") String email) {
        return patientService.getPatientByEmail(email);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public ResponseEntity<ResponseMessage> removePatient(@PathVariable("email") String email) {
        ResponseMessage responseMessage = patientService.removePatientByEmail(email);
        return ResponseEntity.ok(responseMessage);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PatientDTO addPatient(@RequestBody Patient patient) {
        Patient savedPatient = patientService.addPatient(patient);
        return patientMapper.toDTO(savedPatient);
    }

    @PutMapping("/{email}")
    public PatientDTO editPatient(@PathVariable String email, @RequestBody Patient patient) {
        return patientService.editPatientByEmail(email, patient);
    }

    @PatchMapping("/{email}/password")
    public Patient editPatientPassword(@PathVariable String email, @RequestBody ChangePasswordRequest request) {
        return patientService.changePassword(email, request.password());
    }
}
