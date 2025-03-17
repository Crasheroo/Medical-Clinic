package com.example.medicalclinic.mapper;

import com.example.medicalclinic.model.entity.Patient;
import com.example.medicalclinic.model.dto.PatientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(source = "patient", target = "fullName", qualifiedByName = "toFullName")
    PatientDTO toDTO (Patient patient);

    @Named("toFullName")
    default String mapToFullName(Patient patient) {
        if (patient == null) {
            return "";
        }
        return patient.getFirstName() + " " + patient.getLastName();
    }
}
