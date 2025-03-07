package com.example.medicalclinic.mapper;

import com.example.medicalclinic.model.Doctor;
import com.example.medicalclinic.model.DoctorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mapping(target = "facilityNames", source = "facilities", qualifiedByName = "mapFacilityNames")
    DoctorDTO toDTO (Doctor doctor);
    List<DoctorDTO> toDTOList (List<Doctor> doctors);
    List<Doctor> toEntityList (List<DoctorDTO> doctors);

    @Named("mapFacilityNames")
    default List<String> mapFacilityNames(List<com.example.medicalclinic.model.Facility> facilities) {
        if (facilities == null) return List.of();
        return facilities.stream()
                .map(com.example.medicalclinic.model.Facility::getFacilityName)
                .collect(Collectors.toList());
    }
}
