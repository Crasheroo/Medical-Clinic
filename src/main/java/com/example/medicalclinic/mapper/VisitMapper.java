package com.example.medicalclinic.mapper;

import com.example.medicalclinic.model.dto.DoctorDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.entity.Doctor;
import com.example.medicalclinic.model.entity.Facility;
import com.example.medicalclinic.model.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    @Mapping(target = "isAvailable", source = "visit", qualifiedByName = "mapIsAvailable")
    @Mapping(target = "startTime", source = "startTime")
    @Mapping(target = "endTime", source = "endTime")
    @Mapping(target = "doctor", source = "doctor", qualifiedByName = "mapDoctor")
    VisitDTO toDto(Visit visit);

    @Named("mapIsAvailable")
    default boolean mapIsAvailable(Visit visit) {
        return visit.getPatient() == null;
    }

    @Named("mapDoctor")
    default DoctorDTO mapDoctor(Doctor doctor) {
        if (doctor == null) {
            return null;
        }

        return DoctorDTO.builder()
                .id(doctor.getId())
                .email(doctor.getEmail())
                .facilityIds(doctor.getFacilities().stream()
                        .map(Facility::getId)
                        .toList())
                .build();
    }
}
