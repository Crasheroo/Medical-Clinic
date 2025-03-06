package com.example.medicalclinic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "FACILITY")
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String facilityName;
    private String city;
    private String postcode;
    private String street;
    private String buildingNumber;
    @ManyToMany
    @JsonIgnore
    private List<Doctor> doctors = new ArrayList<>();

    public List<String> getDoctorEmails() {
        return doctors.stream()
                .map(Doctor::getEmail)
                .collect(Collectors.toList());
    }

    public void updateFrom(Facility updatedFacility) {
        if (updatedFacility.getFacilityName() != null) this.setFacilityName(updatedFacility.getFacilityName());
        if (updatedFacility.getCity() != null) this.setCity(updatedFacility.getCity());
        if (updatedFacility.getPostcode() != null) this.setPostcode(updatedFacility.getPostcode());
        if (updatedFacility.getStreet() != null) this.setStreet(updatedFacility.getStreet());
        if (updatedFacility.getBuildingNumber() != null) this.setBuildingNumber(updatedFacility.getBuildingNumber());
    }
}
