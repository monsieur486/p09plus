package com.mr486.msrisque.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.mr486.commun.dto.NoteDto;
import com.mr486.commun.dto.PageDto;
import com.mr486.commun.exception.ServiceIndisponibleException;
import com.mr486.msrisque.client.NoteClient;
import feign.FeignException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("Récupération des notes d'un patient")
class NotesServiceTest {
    private static final Long PATIENT_ID = 2L;

    @Mock
    private NoteClient noteClient;

    @InjectMocks
    private NotesService notesService;

    @Test
    @DisplayName("toutes les pages de notes sont parcourues, pas seulement la première")
    void plusieursPages_sontToutesParcourues() {
        when(noteClient.getNotesByPatientId(eq(PATIENT_ID), eq(0), anyInt()))
                .thenReturn(page(List.of(new NoteDto("Taille"), new NoteDto("Poids")), 0, 2));
        when(noteClient.getNotesByPatientId(eq(PATIENT_ID), eq(1), anyInt()))
                .thenReturn(page(List.of(new NoteDto("Fumeur")), 1, 2));

        List<NoteDto> notes = notesService.getNotesByPatientId(PATIENT_ID);

        assertThat(notes).hasSize(3);
        assertThat(notes).extracting(NoteDto::getContent)
                .containsExactly("Taille", "Poids", "Fumeur");
    }

    @Test
    @DisplayName("une page unique n'entraîne pas d'appel supplémentaire")
    void pageUnique_retourneSonContenu() {
        when(noteClient.getNotesByPatientId(eq(PATIENT_ID), eq(0), anyInt()))
                .thenReturn(page(List.of(new NoteDto("Vertige")), 0, 1));

        assertThat(notesService.getNotesByPatientId(PATIENT_ID)).hasSize(1);
    }

    @Test
    @DisplayName("un patient sans note retourne une liste vide sans erreur")
    void aucuneNote_retourneListeVide() {
        when(noteClient.getNotesByPatientId(eq(PATIENT_ID), eq(0), anyInt()))
                .thenReturn(page(List.of(), 0, 0));

        assertThat(notesService.getNotesByPatientId(PATIENT_ID)).isEmpty();
    }

    @Test
    @DisplayName("un service des notes injoignable devient une indisponibilité de service")
    void serviceInjoignable_leveServiceIndisponible() {
        when(noteClient.getNotesByPatientId(eq(PATIENT_ID), anyInt(), anyInt()))
                .thenThrow(mock(FeignException.ServiceUnavailable.class));

        assertThatThrownBy(() -> notesService.getNotesByPatientId(PATIENT_ID))
                .isInstanceOf(ServiceIndisponibleException.class)
                .hasMessageContaining("ms-notes");
    }

    @Test
    @DisplayName("une ressource absente côté service des notes retourne une liste vide")
    void ressourceAbsente_retourneListeVide() {
        when(noteClient.getNotesByPatientId(eq(PATIENT_ID), anyInt(), anyInt()))
                .thenThrow(mock(FeignException.NotFound.class));

        assertThat(notesService.getNotesByPatientId(PATIENT_ID)).isEmpty();
    }

    private PageDto<NoteDto> page(List<NoteDto> contenu, int numero, int totalDePages) {
        return PageDto.<NoteDto>builder()
                .contenu(contenu)
                .page(numero)
                .taille(contenu.size())
                .totalElements(contenu.size())
                .totalPages(totalDePages)
                .build();
    }
}
