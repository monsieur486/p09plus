package com.mr486.commun.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Tranche de résultats renvoyée par une API paginée.
 *
 * <p>Ce type expose le strict nécessaire à l'affichage d'une pagination — le contenu de la
 * page et de quoi construire les liens de navigation — plutôt que la {@code Page} de Spring
 * Data, dont la sérialisation n'est pas un contrat stable.</p>
 *
 * <p><b>Exemple :</b> {@code GET /patients?page=0&size=10} sur 47 patients retourne une
 * page dont {@code contenu} porte 10 éléments, {@code totalElements} vaut 47 et
 * {@code totalPages} vaut 5.</p>
 *
 * @param <T> type des éléments portés par la page
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageDto<T> {

    /** Éléments de la page demandée. */
    private List<T> contenu;

    /** Numéro de la page, compté à partir de zéro. */
    private int page;

    /** Nombre d'éléments demandés par page. */
    private int taille;

    /** Nombre total d'éléments, toutes pages confondues. */
    private long totalElements;

    /** Nombre total de pages disponibles. */
    private int totalPages;

    /**
     * Indique s'il existe une page suivante.
     *
     * <p><b>Exemple :</b> sur la dernière page, {@code aSuivante()} retourne
     * {@code false}.</p>
     *
     * @return {@code true} si une page suivante peut être demandée
     */
    public boolean aSuivante() {
        return page + 1 < totalPages;
    }

    /**
     * Indique s'il existe une page précédente.
     *
     * <p><b>Exemple :</b> sur la première page, {@code aPrecedente()} retourne
     * {@code false}.</p>
     *
     * @return {@code true} si une page précédente peut être demandée
     */
    public boolean aPrecedente() {
        return page > 0;
    }
}
