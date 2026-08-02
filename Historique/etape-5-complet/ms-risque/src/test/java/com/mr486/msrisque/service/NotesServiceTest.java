package com.mr486.msrisque.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mr486.commun.exception.ServiceIndisponibleException;
import com.mr486.msrisque.client.NoteClient;
import feign.FeignException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests de la récupération des notes auprès du microservice dédié.
 *
 * <p>Le comportement qui porte une décision est la traduction des échecs d'appel : une
 * ressource absente est un dossier vide, tandis qu'un service injoignable est une panne
 * qui doit remonter plutôt que produire une évaluation faussement rassurante.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Récupération des notes d'un patient")
class NotesServiceTest {

    /** Identifiant du patient manipulé par les scénarios. */
    private static final Long PATIENT_ID = 2L;

    @Mock
    private NoteClient noteClient;

    private NotesService notesService;

    @BeforeEach
    void initialiseLeService() {
        notesService = new NotesService(noteClient);
    }

    @Test
    @DisplayName("un patient sans note retourne une liste vide sans erreur")
    void aucuneNote_retourneListeVide() {
        when(noteClient.getNotesByPatientId(PATIENT_ID)).thenReturn(List.of());

        assertThat(notesService.getNotesByPatientId(PATIENT_ID)).isEmpty();
    }

    @Test
    @DisplayName("une réponse sans corps retourne une liste vide")
    void reponseNulle_retourneListeVide() {
        when(noteClient.getNotesByPatientId(PATIENT_ID)).thenReturn(null);

        assertThat(notesService.getNotesByPatientId(PATIENT_ID)).isEmpty();
    }

    @Test
    @DisplayName("un service des notes injoignable devient une indisponibilité de service")
    void serviceInjoignable_leveServiceIndisponible() {
        when(noteClient.getNotesByPatientId(PATIENT_ID))
                .thenThrow(mock(FeignException.ServiceUnavailable.class));

        assertThatThrownBy(() -> notesService.getNotesByPatientId(PATIENT_ID))
                .isInstanceOf(ServiceIndisponibleException.class)
                .hasMessageContaining("ms-notes");
    }

    @Test
    @DisplayName("une ressource absente côté service des notes retourne une liste vide")
    void ressourceAbsente_retourneListeVide() {
        when(noteClient.getNotesByPatientId(PATIENT_ID))
                .thenThrow(mock(FeignException.NotFound.class));

        assertThat(notesService.getNotesByPatientId(PATIENT_ID)).isEmpty();
    }
}
