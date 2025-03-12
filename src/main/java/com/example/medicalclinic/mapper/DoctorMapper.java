package com.example.medicalclinic.mapper;

import com.example.medicalclinic.model.Doctor;
import com.example.medicalclinic.dto.DoctorDTO;
import com.example.medicalclinic.model.Facility;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "facilityIds", source = "facilities", qualifiedByName = "mapFacilityIds")
    DoctorDTO toDTO(Doctor doctor);

    List<DoctorDTO> toDTOList(List<Doctor> doctors);

    List<Doctor> toEntityList(List<DoctorDTO> doctorDTOs);

    @Named("mapFacilityIds")
    static List<Long> mapFacilityIds(Set<Facility> facilities) {
        if (facilities == null) {
            return List.of();
        }
        return facilities.stream()
                .map(Facility::getId)
                .toList();
    }
}
