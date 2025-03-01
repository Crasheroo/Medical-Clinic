package com.example.medicalclinic.mapper;

import com.example.medicalclinic.model.Patient;
import com.example.medicalclinic.model.PatientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PatientMapper {
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    @Mapping(source = "patient", target = "fullName", qualifiedByName = "toFullName")
    PatientDTO toDTO(Patient patient);

    List<PatientDTO> toDTOList(List<Patient> patients);

    @Named("toFullName")
    default String mapToFullName(Patient patient) {
        if (patient == null) {
            return "";
        }
        return patient.getFirstName() + " " + patient.getLastName();
    }
}
