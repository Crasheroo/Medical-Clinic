package com.example.medicalclinic.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@Builder
public class FacilityDTO {
    private Long id;
    private String facilityName;
    private String city;
    private String postcode;
    private String street;
    private String buildingNumber;
    private Set<Long> doctorIds;
}
