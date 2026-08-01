package com.mr486.msnotes.repository;

import com.mr486.msnotes.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    Page<Note> findByPatientIdOrderByCreatedDateDesc(Long patientId, Pageable pagination);
}
