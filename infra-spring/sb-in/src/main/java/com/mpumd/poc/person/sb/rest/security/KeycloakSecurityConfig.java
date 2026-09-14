package com.mpumd.poc.person.sb.rest.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Authentication concern of the REST in-adapter, delegated to keycloak which
 * plays the authority : it hosts the users and their roles, we only validate
 * the jwt it signed.
 * <p>
 * Authorization stays on the application service with method security, this
 * config only feeds the authentication with the right authorities.
 */
@Configuration
@EnableWebSecurity
@Profile("keycloak")
public class KeycloakSecurityConfig {

    @Bean
    SecurityFilterChain keycloakSecurityFilterChain(HttpSecurity http,
                                                    JwtAuthenticationConverter jwtAuthenticationConverter) {
        return http.csrf(CsrfConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health/liveness", "/actuator/health/readiness")
                        .permitAll() // kube health check
                        // every other actuator endpoint is ops material. With exposure.include "*"
                        // this covers, among others :
                        //   health          full health with details and components (db, disk, ...)
                        //   info            build / env / git info
                        //   metrics         micrometer meters, and prometheus if the registry is on the path
                        //   env             environment and every property source, may leak secrets
                        //   configprops     @ConfigurationProperties beans, may leak secrets
                        //   beans           the whole spring bean graph
                        //   conditions      auto-configuration condition report
                        //   mappings        every @RequestMapping route
                        //   loggers         read and change log levels at runtime
                        //   threaddump      full jvm thread dump
                        //   heapdump        downloadable heap dump, sensitive
                        //   scheduledtasks  scheduled tasks
                        //   caches          caches
                        //   sbom            software bill of materials
                        //   liquibase       applied changesets (liquibase is used here)
                        // the SB admin ui is a graphical window on the very same data, so
                        // it deserves the same ops-only treatment

                        .requestMatchers("/actuator/**", "/admin/**")
                        .hasRole("OPS") // the kube cluster must have OPS role.

                        .requestMatchers("/swagger-ui/**", "/api-docs*", "/api-docs/**")
                        .permitAll() // because of they are disabled by properties in prod

                        // business endpoint
                        .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .build();
    }

    /**
     * Keycloak carries the realm roles in the {@code realm_access.roles} claim
     * whereas spring reads {@code scope} by default. Without that bridge every
     * hasRole() of the application service would silently deny.
     */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(KeycloakSecurityConfig::convertKeycloakToSpringSecurityRole);
        return converter;
    }

    private static Collection<GrantedAuthority> convertKeycloakToSpringSecurityRole(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || !(realmAccess.get("roles") instanceof List<?> roles)) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(String::valueOf)
                // ROLE_ prefix is what hasRole() expects
                .<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
