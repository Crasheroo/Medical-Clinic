package com.example.demo.controller;

import com.example.demo.model.Patient;
import com.example.demo.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<Patient> getPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/email")
    public Patient getPatientByEmail(@PathVariable("email") String email) {
        return patientService.getPatientByEmail(email);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/email/{email}")
    public void removePatient(@PathVariable("email") String email) {
        patientService.removePatientByEmail(email);
    }

    @PostMapping
    public Patient addNewPatient(@RequestBody Patient patient) {
        return patientService.addNewPatient(patient);
    }

    @PutMapping("/email/{email}")
    public Patient editPatient(@PathVariable String email, @RequestBody Patient patient) {
        return patientService.editPatientByEmail(email, patient.getPassword(), patient.getIdCardNo(), patient.getFirstName(), patient.getLastName(), patient.getPhoneNumber(), patient.getBirthday());
    }

}
