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
        return http
                // stateless REST api, credentials are carried by each request
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // POC : monitoring (actuator + SB admin) and api doc stay open
                        .requestMatchers( // FIXME disable by default and override only in local and dev
                                "/actuator/**", "/admin/**", "/swagger-ui/**",
                                "/api-docs*", "/api-docs/**")
                        .permitAll()
                        // business request
                        .anyRequest().authenticated()
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
            return List.of();
        }
        return roles.stream()
                .map(String::valueOf)
                // ROLE_ prefix is what hasRole() expects
                .<GrantedAuthority>map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
    }
}
