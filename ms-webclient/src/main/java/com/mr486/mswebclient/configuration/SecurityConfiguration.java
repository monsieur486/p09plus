package com.mr486.mswebclient.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Sécurise l'interface web des praticiens.
 *
 * <p>À la différence des API internes, protégées en HTTP Basic sans session, l'interface
 * s'adresse à un navigateur : elle utilise donc un formulaire de connexion et une session,
 * et conserve la protection CSRF activée par défaut. Seuls l'accueil et la page de
 * connexion sont ouverts ; tout le reste exige une authentification.</p>
 *
 * <p><b>Exemple :</b> accéder à {@code /app/dashboard} sans être connecté redirige vers
 * {@code /login} ; après connexion, l'utilisateur arrive sur le tableau de bord.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Chemins accessibles sans authentification.
     *
     * <p>Les ressources statiques en font partie : la feuille de style et les scripts sont
     * nécessaires à l'affichage de la page de connexion elle-même. Les protéger les ferait
     * rediriger vers cette page, qui s'afficherait alors sans mise en forme.</p>
     */
    private static final String[] PAGES_PUBLIQUES = {"/", "/home", "/webjars/**"};

    /** Identifiant du compte praticien, injecté depuis la configuration. */
    @Value("${app.auth.username}")
    private String utilisateur;

    /** Mot de passe du compte praticien, injecté depuis la configuration. */
    @Value("${app.auth.password}")
    private String motDePasse;

    /**
     * Définit la chaîne de filtres appliquée aux pages de l'interface.
     *
     * <p><b>Exemple :</b> la déconnexion supprime le cookie de session et ramène à la page
     * d'accueil.</p>
     *
     * @param http constructeur de la configuration de sécurité web
     * @return la chaîne de filtres à appliquer
     * @throws Exception si la configuration de la chaîne échoue
     */
    @Bean
    public SecurityFilterChain filtreDeSecurite(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requetes -> requetes
                        .requestMatchers(PAGES_PUBLIQUES).permitAll()
                        .anyRequest().authenticated())
                .formLogin(formulaire -> formulaire
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/app/dashboard", true)
                        .permitAll())
                .logout(deconnexion -> deconnexion
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .permitAll());
        return http.build();
    }

    /**
     * Fournit l'algorithme de hachage des mots de passe.
     *
     * <p><b>Exemple :</b> le mot de passe issu de la configuration est haché par BCrypt
     * avant d'être comparé à celui saisi dans le formulaire.</p>
     *
     * @return l'encodeur BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Déclare l'unique compte praticien autorisé à se connecter.
     *
     * <p><b>Exemple :</b> avec les valeurs par défaut, le compte {@code app_user} est le
     * seul reconnu.</p>
     *
     * @param encodeur encodeur utilisé pour hacher le mot de passe configuré
     * @return le service de consultation des comptes, alimenté en mémoire
     */
    @Bean
    public UserDetailsService utilisateurs(PasswordEncoder encodeur) {
        return new InMemoryUserDetailsManager(
                User.withUsername(utilisateur)
                        .password(encodeur.encode(motDePasse))
                        .roles("USER")
                        .build());
    }
}
