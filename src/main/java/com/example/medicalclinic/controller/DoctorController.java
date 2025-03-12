package com.example.medicalclinic.controller;

import com.example.medicalclinic.dto.DoctorDTO;
import com.example.medicalclinic.mapper.DoctorMapper;
import com.example.medicalclinic.model.*;
import com.example.medicalclinic.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public List<DoctorDTO> getDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return doctorService.getAllDoctors(PageRequest.of(page, size));
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

    @PostMapping("/{doctorId}/facilities/{facilityId}")
    public DoctorDTO assignDoctorToFacility(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        return doctorService.assignDoctorToFacility(doctorId, facilityId);
    }

    @DeleteMapping("/{doctorId}/facilities/{facilityId}")
    public void removeFacilityFromDoctor(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        doctorService.removeFacilityFromDoctor(doctorId, facilityId);
    }
}