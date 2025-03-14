package com.example.medicalclinic.mapper;

import com.example.medicalclinic.model.Doctor;
import com.example.medicalclinic.dto.DoctorDTO;
import com.example.medicalclinic.model.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.*;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "facilityIds", source = "facilities", qualifiedByName = "mapFacilityIds")
    DoctorDTO toDTO(Doctor doctor);
    Doctor toEntity(DoctorDTO doctorDTO);

    @Named("mapFacilityIds")
    static List<Long> mapFacilityIds(Set<Facility> facilities) {
        return Optional.ofNullable(facilities).orElse(Collections.emptySet()).stream()
                .map(Facility::getId)
                .toList();
    }
}
