package com.mr486.commun.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Sécurise de façon identique les API internes des microservices.
 *
 * <p>Ces API ne sont pas exposées à des navigateurs mais appelées de service à service :
 * l'authentification se fait donc en HTTP Basic, sans session. La protection CSRF est
 * désactivée en conséquence — elle ne défend que les échanges portés par un cookie de
 * session, absent ici — et seuls la supervision et le contrat OpenAPI restent ouverts.</p>
 *
 * <p>Les identifiants proviennent de la configuration ({@code security.app-user.*}),
 * elle-même alimentée par les variables d'environnement : aucun secret n'est écrit en dur.
 * Le mot de passe n'est jamais conservé en clair, il est haché avec BCrypt au démarrage.</p>
 *
 * <p><b>Exemple :</b> un appel sans en-tête d'authentification sur {@code /patients}
 * retourne un statut 401, alors que {@code /actuator/health} reste accessible.</p>
 */
@Configuration
@EnableWebSecurity
public class SecuriteApiConfiguration {

    /** Chemins ouverts sans authentification : supervision et contrat OpenAPI. */
    private static final String[] CHEMINS_PUBLICS = {"/actuator/**", "/v3/api-docs/**"};

    /** Identifiant du compte de service, injecté depuis la configuration. */
    // Non final malgré creedengo : Spring affecte la valeur après avoir construit l'objet,
    // ce qu'un champ final rendrait impossible. Vaut pour les deux champs qui suivent.
    @SuppressWarnings("creedengo-java:GCI82")
    @Value("${security.app-user.username}")
    private String utilisateur;

    /** Mot de passe du compte de service, injecté depuis la configuration. */
    @SuppressWarnings("creedengo-java:GCI82")
    @Value("${security.app-user.password}")
    private String motDePasse;

    /**
     * Définit la chaîne de filtres appliquée aux appels entrants.
     *
     * <p><b>Exemple :</b> {@code GET /patients/1} exige un en-tête
     * {@code Authorization: Basic …} valide, sinon la réponse est un statut 401.</p>
     *
     * @param http constructeur de la configuration de sécurité web
     * @return la chaîne de filtres à appliquer
     * @throws Exception si la configuration de la chaîne échoue
     */
    @Bean
    // creedengo vise ici le paramètre des lambdas de configuration. Le rendre final
    // obligerait à en écrire le type, que le compilateur déduit aujourd'hui : la chaîne de
    // filtres deviendrait illisible pour un gain nul.
    @SuppressWarnings("creedengo-java:GCI82")
    public SecurityFilterChain filtreDeSecurite(final HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requetes -> requetes
                        .requestMatchers(CHEMINS_PUBLICS).permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    /**
     * Fournit l'algorithme de hachage des mots de passe.
     *
     * <p><b>Exemple :</b> le mot de passe issu de la configuration est haché par BCrypt
     * avant d'être comparé à celui présenté par l'appelant.</p>
     *
     * @return l'encodeur BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Déclare l'unique compte de service autorisé à appeler l'API.
     *
     * <p><b>Exemple :</b> avec les valeurs par défaut, le compte {@code app_user} est le
     * seul reconnu.</p>
     *
     * @param encodeur encodeur utilisé pour hacher le mot de passe configuré
     * @return le service de consultation des comptes, alimenté en mémoire
     */
    @Bean
    public UserDetailsService utilisateurs(final PasswordEncoder encodeur) {
        return new InMemoryUserDetailsManager(
                User.withUsername(utilisateur)
                        .password(encodeur.encode(motDePasse))
                        .roles("USER")
                        .build());
    }
}
