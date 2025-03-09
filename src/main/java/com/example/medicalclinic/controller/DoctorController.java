package com.example.medicalclinic.controller;

import com.example.medicalclinic.mapper.DoctorMapper;
import com.example.medicalclinic.model.ChangePasswordRequest;
import com.example.medicalclinic.model.Doctor;
import com.example.medicalclinic.model.DoctorDTO;
import com.example.medicalclinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @GetMapping
    public List<DoctorDTO> getDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/{email}")
    public DoctorDTO getDoctorByEmail(@PathVariable("email") String email) {
        return doctorService.getDoctorByEmail(email);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{email}")
    public void removeDoctor(@PathVariable("email") String email) {
        doctorService.removeDoctorByEmail(email);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public DoctorDTO addDoctor(@RequestBody Doctor doctor) {
        return doctorMapper.toDTO(doctorService.addDoctor(doctor));
    }

    @PutMapping("/{email}")
    public DoctorDTO editDoctor(@PathVariable String email, @RequestBody Doctor doctor) {
        return doctorService.editDoctorByEmail(email, doctor);
    }

    @PatchMapping("/{email}/password")
    public Doctor editDoctorPassword(@PathVariable String email, @RequestBody ChangePasswordRequest request) {
        return doctorService.changePassword(email, request.password());
    }

    @PostMapping("/{doctorId}/facilities/{facilityName}")
    public DoctorDTO assignDoctorToFacility(@PathVariable Long doctorId, @PathVariable String facilityName) {
        return doctorService.assignDoctorToFacility(doctorId, facilityName);
    }

    @DeleteMapping("/{email}/facilities/{facilityName}")
    public void removeFacilityFromDoctor(@PathVariable String email, @PathVariable String facilityName) {
        doctorService.removeFacilityFromDoctor(email, facilityName);
    }

}