package com.mr486.msnotes.dbchangelogs;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Alimente la base documentaire avec le jeu de notes de démonstration.
 *
 * <p>Ces notes correspondent aux quatre patients de démonstration créés par les migrations
 * du service des patients, et couvrent les quatre niveaux de risque. Les modifier fait
 * diverger les résultats attendus de l'évaluation.</p>
 *
 * <p>La migration ne s'exécute que si la collection est vide : une base déjà alimentée,
 * en production comme en recette, n'est jamais altérée.</p>
 *
 * <p><b>Exemple :</b> au premier démarrage sur une base neuve, neuf notes sont insérées ;
 * aux démarrages suivants, la migration ne fait rien.</p>
 */
@ChangeUnit(id = "seed-demo-notes", order = "001", author = "mr486")
@Slf4j
public class DataBaseChangeLog {

    /** Nom de la collection recevant les notes. */
    private static final String COLLECTION = "notes";

    /** Nom du champ portant l'identifiant du patient. */
    private static final String CHAMP_PATIENT = "patientId";

    /** Nom du champ portant le texte de la note. */
    private static final String CHAMP_CONTENU = "content";

    /** Nom du champ portant la date d'enregistrement. */
    private static final String CHAMP_DATE = "createdDate";

    /** Patient de démonstration attendu au niveau de risque {@code None}. */
    private static final long PATIENT_SANS_RISQUE = 1L;

    /** Patient de démonstration attendu au niveau de risque {@code Borderline}. */
    private static final long PATIENT_RISQUE_LIMITE = 2L;

    /** Patient de démonstration attendu au niveau de risque {@code In Danger}. */
    private static final long PATIENT_EN_DANGER = 3L;

    /** Patient de démonstration attendu au niveau de risque {@code Early onset}. */
    private static final long PATIENT_RISQUE_PRECOCE = 4L;

    /**
     * Notes de démonstration, dans leur ordre d'insertion.
     *
     * <p>Leur rang détermine la date d'enregistrement, donc l'ordre d'affichage : la
     * dernière insérée est la plus récente.</p>
     */
    // creedengo déconseille les collections statiques, qui retiennent la mémoire et
    // grossissent sans contrôle. Celle-ci est immuable (List.of) et figée : c'est le jeu de
    // démonstration, lu une seule fois au premier démarrage.
    @SuppressWarnings("creedengo-java:GCI76")
    private static final List<NoteDeDemonstration> MODELES = List.of(
            new NoteDeDemonstration(PATIENT_SANS_RISQUE,
                    "Le patient déclare qu'il 'se sent très bien' Poids égal ou inférieur au "
                            + "poids recommandé"),
            new NoteDeDemonstration(PATIENT_RISQUE_LIMITE,
                    "Le patient déclare qu'il ressent beaucoup de stress au travail Il se plaint "
                            + "également que son audition est anormale dernièrement"),
            new NoteDeDemonstration(PATIENT_RISQUE_LIMITE,
                    "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 "
                            + "derniers mois Il remarque également que son audition continue "
                            + "d'être anormale"),
            new NoteDeDemonstration(PATIENT_EN_DANGER,
                    "Le patient déclare qu'il fume depuis peu"),
            new NoteDeDemonstration(PATIENT_EN_DANGER,
                    "Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année "
                            + "dernière Il se plaint également de crises d’apnée respiratoire "
                            + "anormales Tests de laboratoire indiquant un taux de cholestérol "
                            + "LDL élevé"),
            new NoteDeDemonstration(PATIENT_RISQUE_PRECOCE,
                    "Le patient déclare qu'il lui est devenu difficile de monter les escaliers Il "
                            + "se plaint également d’être essoufflé Tests de laboratoire indiquant "
                            + "que les anticorps sont élevés Réaction aux médicaments"),
            new NoteDeDemonstration(PATIENT_RISQUE_PRECOCE,
                    "Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps"),
            new NoteDeDemonstration(PATIENT_RISQUE_PRECOCE,
                    "Le patient déclare avoir commencé à fumer depuis peu Hémoglobine A1C "
                            + "supérieure au niveau recommandé"),
            new NoteDeDemonstration(PATIENT_RISQUE_PRECOCE,
                    "Taille, Poids, Cholestérol, Vertige et Réaction"));

    private final MongoTemplate mongoTemplate;

    /**
     * Construit la migration avec l'accès à la base documentaire.
     *
     * <p><b>Exemple :</b> Mongock instancie cette classe au démarrage et lui fournit le
     * {@link MongoTemplate} de l'application.</p>
     *
     * @param mongoTemplate accès à la base documentaire
     */
    public DataBaseChangeLog(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Insère les notes de démonstration si la collection est vide.
     *
     * <p><b>Exemple :</b> sur une base neuve, le patient 4 reçoit les quatre notes qui le
     * placent au niveau de risque le plus élevé.</p>
     */
    @Execution
    public void seedDemoNotesIfEmpty() {
        if (mongoTemplate.getCollection(COLLECTION).countDocuments() > 0) {
            log.info("notes de démonstration déjà présentes, insertion ignorée");
            return;
        }

        final List<Document> notes = construitLesNotesDeDemonstration();
        mongoTemplate.getCollection(COLLECTION).insertMany(notes);
        log.info("{} notes de démonstration insérées", notes.size());
    }

    /**
     * Retire les notes insérées par cette migration.
     *
     * <p><b>Exemple :</b> une annulation de la migration vide entièrement la collection des
     * notes.</p>
     */
    @RollbackExecution
    public void rollback() {
        mongoTemplate.getCollection(COLLECTION).deleteMany(new Document());
        log.warn("notes de démonstration supprimées par annulation de la migration");
    }

    // Assemble le jeu de démonstration ; le rang de chaque note décale sa date d'une seconde.
    private List<Document> construitLesNotesDeDemonstration() {
        // Zone explicite : c'est déjà celle qu'utilise versInstant pour la conversion.
        final LocalDateTime maintenant = LocalDateTime.now(ZoneId.systemDefault());
        final List<Document> notes = new ArrayList<>();
        for (final NoteDeDemonstration modele : MODELES) {
            notes.add(new Document()
                    .append(CHAMP_PATIENT, modele.patientId())
                    .append(CHAMP_CONTENU, modele.contenu())
                    .append(CHAMP_DATE, versInstant(maintenant.plusSeconds(notes.size()))));
        }
        return notes;
    }

    // Convertit une date locale en instant, type attendu par le pilote MongoDB.
    private Instant versInstant(final LocalDateTime dateHeure) {
        return dateHeure.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Note de démonstration à insérer.
     *
     * @param patientId identifiant du patient auquel la note est rattachée
     * @param contenu   texte de la note
     */
    // Les composants d'un record n'admettent aucun modificateur : ils sont déjà finaux, et
    // le langage refuse qu'on l'écrive. La remarque de creedengo est ici sans objet.
    @SuppressWarnings("creedengo-java:GCI82")
    private record NoteDeDemonstration(long patientId, String contenu) {
    }
}
