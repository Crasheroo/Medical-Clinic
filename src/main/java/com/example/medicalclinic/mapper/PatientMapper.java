package com.example.medicalclinic.mapper;

import com.example.medicalclinic.model.Patient;
import com.example.medicalclinic.dto.PatientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(source = "patient", target = "fullName", qualifiedByName = "toFullName")
    PatientDTO toDTO (Patient patient);

    List<PatientDTO> toDTOList (List<Patient> patients);
    List<Patient> toEntityList (List<PatientDTO> patients);

    @Named("toFullName")
    default String mapToFullName(Patient patient) {
        if (patient == null) {
            return "";
        }
        return patient.getFirstName() + " " + patient.getLastName();
    }
}
