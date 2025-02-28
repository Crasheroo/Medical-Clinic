package com.example.medicalclinic.mapper;

import com.example.medicalclinic.model.Patient;
import com.example.medicalclinic.model.PatientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PatientMapper {
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    PatientDTO toDTO(Patient patient);
    List<PatientDTO> toDTOList(List<Patient> patients);
}
