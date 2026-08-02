package com.mr486.mspatients.mapper;

import com.mr486.commun.dto.PatientDto;
import com.mr486.mspatients.model.Patient;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {
    public PatientDto versDto(Patient patient) {
        return PatientDto.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .birthDate(patient.getBirthDate())
                .gender(patient.getGender())
                .postalAddress(patient.getPostalAddress())
                .phoneNumber(patient.getPhoneNumber())
                .build();
    }

    public List<PatientDto> versListeDto(List<Patient> patients) {
        return patients.stream().map(this::versDto).toList();
    }
}
