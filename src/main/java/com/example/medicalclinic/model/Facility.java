package com.example.medicalclinic.model;

import com.example.medicalclinic.dto.FacilityRequestDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

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
    @ManyToMany(mappedBy = "facilities", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Doctor> doctors = new HashSet<>();

    public void updateFrom(Facility other) {
        Optional.ofNullable(other.getFacilityName()).ifPresent(newEmail -> this.facilityName = newEmail);
        Optional.ofNullable(other.getCity()).ifPresent(newEmail -> this.city = newEmail);
        Optional.ofNullable(other.getPostcode()).ifPresent(newEmail -> this.postcode = newEmail);
        Optional.ofNullable(other.getStreet()).ifPresent(newEmail -> this.street = newEmail);
        Optional.ofNullable(other.getBuildingNumber()).ifPresent(newEmail -> this.buildingNumber = newEmail);
    }

    public static Facility from(FacilityRequestDTO request) {
        return Facility.builder()
                .facilityName(request.getFacilityName())
                .city(request.getCity())
                .postcode(request.getPostcode())
                .street(request.getStreet())
                .buildingNumber(request.getBuildingNumber())
                .doctors(new HashSet<>())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Facility))
            return false;

        Facility other = (Facility) o;

        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
