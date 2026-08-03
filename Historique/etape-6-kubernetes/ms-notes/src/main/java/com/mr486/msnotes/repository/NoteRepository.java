package com.mr486.msnotes.repository;

import com.mr486.msnotes.model.Note;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Accès aux notes stockées dans la base documentaire.
 *
 * <p>L'identifiant technique est une chaîne : c'est le type de l'{@code ObjectId} MongoDB
 * tel que porté par {@link Note}.</p>
 *
 * <p><b>Exemple :</b> {@code findByPatientIdOrderByCreatedDateDesc(2L)} retourne les notes
 * du patient 2, la plus récente en tête.</p>
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    /**
     * Retourne les notes d'un patient, de la plus récente à la plus ancienne.
     *
     * <p><b>Exemple :</b> {@code findByPatientIdOrderByCreatedDateDesc(9L)} retourne une
     * liste vide si le patient n'a aucune note.</p>
     *
     * @param patientId identifiant du patient concerné
     * @return les notes du patient, triées par date d'enregistrement décroissante
     */
    List<Note> findByPatientIdOrderByCreatedDateDesc(final Long patientId);
}
