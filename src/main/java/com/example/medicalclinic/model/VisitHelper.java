package com.example.medicalclinic.model;

import com.example.medicalclinic.exception.VisitException;
import com.example.medicalclinic.mapper.VisitMapper;
import com.example.medicalclinic.model.dto.PageableContentDTO;
import com.example.medicalclinic.model.dto.VisitDTO;
import com.example.medicalclinic.model.dto.VisitFilterDTO;
import com.example.medicalclinic.model.entity.Visit;
import com.example.medicalclinic.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VisitHelper {
    private final VisitMapper visitMapper;
    private final VisitRepository visitRepository;

    public void validateTimes(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new VisitException("Can't create visits in the past", HttpStatus.CONFLICT);
        }
        if (endTime.isBefore(startTime)) {
            throw new VisitException("End time must be after start time", HttpStatus.CONFLICT);
        }
        if (startTime.getMinute() % 15 != 0 || endTime.getMinute() % 15 != 0) {
            throw new VisitException("Visits must be in quarter (00, 15, 30, 45)", HttpStatus.CONFLICT);
        }
    }

    public List<Visit> getConflictingVisits(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        return visitRepository.findByDoctorId(doctorId).stream()
                .filter(visit -> visit.getStartTime().isBefore(endTime) && visit.getEndTime().isAfter(startTime))
                .toList();
    }

    public PageableContentDTO<VisitDTO> filterVisits(VisitFilterDTO filter, VisitRepository visitRepository, Pageable pageable) {
        if (filter == null) {
            return convertToPageableDTO(visitRepository.findAll(pageable));
        }

        if (filter.getPatientEmail() != null) {
            return convertToPageableDTO(
                    visitRepository.findAllByPatientEmail(filter.getPatientEmail(), pageable));
        }

        if (filter.getPatientEmail() != null && filter.getDoctorId() != null) {
            throw new VisitException("Cannot filter both patientEmail and DoctorId", HttpStatus.CONFLICT);
        }

        if (filter.getDoctorId() != null) {
            return filter.isOnlyAvailable()
                    ? convertToPageableDTO(
                    visitRepository.findByDoctorIdAndPatientIsNull(filter.getDoctorId(), pageable))
                    : convertToPageableDTO(
                    visitRepository.findByDoctorId(filter.getDoctorId(), pageable));
        }

        if (filter.getSpeciality() != null && filter.getDate() != null) {
            LocalDateTime startOfDay = filter.getDate().atStartOfDay();
            LocalDateTime endOfDay = filter.getDate().plusDays(1).atStartOfDay();
            return convertToPageableDTO(
                    visitRepository.findByDoctorSpecialtyAndStartTimeBetweenAndPatientIsNull(
                            filter.getSpeciality(), startOfDay, endOfDay, pageable));
        }

        return convertToPageableDTO(visitRepository.findAll(pageable));
    }

    //sekundy minuty godziny dni miesiące dni-tygodnia  * - > każda np godzina
    @Scheduled(cron = "0 0 * * * *")
    public void removePastVisits() {
        List<Visit> pastVisits = visitRepository.findByEndTimeBefore(LocalDateTime.now());
        if (!pastVisits.isEmpty()) {
            visitRepository.deleteAll(pastVisits);
        }
    }

    public PageableContentDTO<VisitDTO> convertToPageableDTO(Page<Visit> visitPage) {
        List<VisitDTO> visits = visitPage.getContent().stream()
                .map(visitMapper::toDto)
                .toList();
        return PageableContentDTO.from(visitPage, visits);
    }
}
