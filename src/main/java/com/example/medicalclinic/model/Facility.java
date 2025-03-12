package com.example.medicalclinic.model;

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

    public void updateFrom(Facility updatedFacility) {
        if (updatedFacility.getFacilityName() != null) {
            this.setFacilityName(updatedFacility.getFacilityName());
        }
        if (updatedFacility.getCity() != null) {
            this.setCity(updatedFacility.getCity());
        }
        if (updatedFacility.getPostcode() != null) {
            this.setPostcode(updatedFacility.getPostcode());
        }
        if (updatedFacility.getStreet() != null) {
            this.setStreet(updatedFacility.getStreet());
        }
        if (updatedFacility.getBuildingNumber() != null) {
            this.setBuildingNumber(updatedFacility.getBuildingNumber());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facility facility)) return false;
        return Objects.equals(id, facility.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
