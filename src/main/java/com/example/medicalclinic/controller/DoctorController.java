package com.example.medicalclinic.controller;

import com.example.medicalclinic.model.ChangePasswordCommand;
import com.example.medicalclinic.model.CreateDoctorCommand;
import com.example.medicalclinic.model.dto.DoctorDTO;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.mapper.DoctorMapper;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;
    private final DoctorMapper doctorMapper;

    @GetMapping
    public PageableContentDTO<DoctorDTO> getDoctors(Pageable pageable) {
        return doctorService.getAllDoctors(pageable);
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
    public DoctorDTO addDoctor(@RequestBody CreateDoctorCommand command) {
        return doctorService.addDoctor(command);
    }

    @PutMapping("/{email}")
    public DoctorDTO editDoctor(@PathVariable String email, @RequestBody CreateDoctorCommand command) {
        return doctorService.editDoctorByEmail(email, command);
    }

    @PostMapping("/{doctorId}/facilities/{facilityId}")
    public DoctorDTO assignDoctorToFacility(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        return doctorService.assignDoctorToFacility(doctorId, facilityId);
    }

    @DeleteMapping("/{doctorId}/facilities/{facilityId}")
    public void removeFacilityFromDoctor(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        doctorService.removeFacilityFromDoctor(doctorId, facilityId);
    }
}