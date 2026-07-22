package com.mpumd.poc.person.sb.rest.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Authentication concern of the REST in-adapter.
 * Role based authorization is not handled here but on the application service
 * with method security, to stay protocol agnostic.
 * <p>
 * Default authority : the application itself, with in memory users.
 * See {@link KeycloakSecurityConfig} for the delegated one.
 */
@Configuration
@EnableWebSecurity
@Profile("!keycloak")
public class SecurityConfig {

    @Bean
    SecurityFilterChain restSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                // stateless REST api, credentials are carried by each request
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // POC : monitoring (actuator + SB admin) and api doc stay open
                        .requestMatchers("/actuator/**", "/admin/**", "/swagger-ui/**", "/api-docs*", "/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    InMemoryUserDetailsManager userDetailsService() {
        // POC : in-memory users with plain text password, never do that in real life
        var user = User.withUsername("user").password("{noop}user").roles("USER").build();
        var admin = User.withUsername("admin").password("{noop}admin").roles("USER", "ADMIN").build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
