package com.mr486.commun.exception;

/**
 * Signale qu'un enregistrement identique existe déjà.
 *
 * <p>Levée par la couche service lorsqu'une création ou une modification violerait une
 * règle d'unicité métier ; les gestionnaires d'exception la traduisent en réponse
 * HTTP 400. L'opération demandée n'est pas appliquée.</p>
 *
 * <p><b>Exemple :</b> {@code throw new DuplicateException("Le patient existe déjà dans
 * la base de données.")} lorsqu'un patient de mêmes nom, prénom, date de naissance et
 * genre est déjà enregistré.</p>
 */
public class DuplicateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construit l'exception avec le message présenté à l'appelant.
     *
     * <p><b>Exemple :</b> {@code new DuplicateException("Le patient existe déjà dans la
     * base de données.")}.</p>
     *
     * @param message description du conflit détecté
     */
    public DuplicateException(final String message) {
        super(message);
    }
}
