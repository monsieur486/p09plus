package com.mr486.mspatients.repository;

import com.mr486.mspatients.model.Patient;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    boolean existsByLastNameAndFirstNameAndBirthDateAndGender(
            String lastName, String firstName, LocalDate birthDate, String gender);
}
