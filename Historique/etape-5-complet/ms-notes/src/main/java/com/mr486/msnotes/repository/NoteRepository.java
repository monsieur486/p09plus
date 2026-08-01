package com.mr486.msnotes.repository;

import com.mr486.msnotes.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Accès aux notes stockées dans la base documentaire.
 *
 * <p>L'identifiant technique est une chaîne : c'est le type de l'{@code ObjectId} MongoDB
 * tel que porté par {@link Note}.</p>
 *
 * <p><b>Exemple :</b> {@code findByPatientIdOrderByCreatedDateDesc(2L, pagination)} retourne
 * la première page des notes du patient 2, la plus récente en tête.</p>
 */
@Repository
public interface NoteRepository extends MongoRepository<Note, String> {

    /**
     * Retourne les notes d'un patient, de la plus récente à la plus ancienne.
     *
     * <p><b>Exemple :</b> {@code findByPatientIdOrderByCreatedDateDesc(9L, pagination)} retourne
     * une page vide si le patient n'a aucune note.</p>
     *
     * @param patientId  identifiant du patient concerné
     * @param pagination page demandée et nombre d'éléments par page
     * @return la page de notes du patient, triée par date d'enregistrement décroissante
     */
    Page<Note> findByPatientIdOrderByCreatedDateDesc(Long patientId, Pageable pagination);
}
