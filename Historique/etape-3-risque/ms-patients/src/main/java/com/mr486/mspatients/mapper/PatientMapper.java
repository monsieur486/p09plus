package com.mr486.mspatients.mapper;

import com.mr486.commun.dto.PageDto;
import com.mr486.commun.dto.PatientDto;
import com.mr486.mspatients.model.Patient;
import org.springframework.data.domain.Page;
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

    public PageDto<PatientDto> versPageDto(Page<Patient> patients) {
        return PageDto.<PatientDto>builder()
                .contenu(patients.getContent().stream().map(this::versDto).toList())
                .page(patients.getNumber())
                .taille(patients.getSize())
                .totalElements(patients.getTotalElements())
                .totalPages(patients.getTotalPages())
                .build();
    }
}
