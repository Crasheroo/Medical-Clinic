package com.example.medicalclinic.mapper;

import com.example.medicalclinic.model.dto.FacilityDTO;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.entity.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    @Mapping(target = "doctorIds", source = "doctors", qualifiedByName = "mapDoctorIds")
    FacilityDTO toDto(Facility facility);
    Facility toEntity(FacilityDTO facilityDTO);
    List<FacilityDTO> listToDto(List<Facility> facilities);

    @Named("mapDoctorIds")
    static Set<Long> mapDoctorIds(Set<Doctor> doctors) {
        return doctors.stream()
                .map(Doctor::getId)
                .collect(Collectors.toSet());
    }
}
