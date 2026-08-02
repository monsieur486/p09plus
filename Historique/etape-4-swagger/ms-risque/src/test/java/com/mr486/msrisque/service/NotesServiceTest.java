package com.mr486.msrisque.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mr486.commun.dto.NoteDto;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("Récupération des notes d'un patient")
class NotesServiceTest {

    private static final Long PATIENT_ID = 2L;

    @Mock
    private NoteClient noteClient;

    private NotesService notesService;

    @BeforeEach
    void initialiseLeService() {
        notesService = new NotesService(noteClient);
    }

    @Test
    @DisplayName("les notes du patient sont remontées dans leur ordre d'origine")
    void notesPresentes_sontRemontees() {
        when(noteClient.getNotesByPatientId(PATIENT_ID))
                .thenReturn(List.of(
                        new NoteDto("Taille"), new NoteDto("Poids"), new NoteDto("Fumeur")));

        List<NoteDto> notes = notesService.getNotesByPatientId(PATIENT_ID);

        assertThat(notes).hasSize(3);
        assertThat(notes).extracting(NoteDto::getContent)
                .containsExactly("Taille", "Poids", "Fumeur");
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
