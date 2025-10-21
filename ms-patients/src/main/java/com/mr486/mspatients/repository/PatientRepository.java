package com.mr486.mspatients.repository;

import com.mr486.mspatients.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
  boolean existsByLastNameAndFirstNameAndBirthDateAndGender(String lastName, String firstName, LocalDate birthDate, String gender);
}
