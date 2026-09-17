package com.mpumd.poc.person.sb.rest.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Edge cases of the keycloak realm roles -> spring authorities bridge, unreachable
 * with a real keycloak which always emits realm_access (default roles).
 * The nominal path is covered against a real token by {@code PersonRestControllerKeycloakIT}.
 */
class KeycloakSecurityConfigTest {

    JwtAuthenticationConverter converter = new KeycloakSecurityConfig().jwtAuthenticationConverter();

    /**
     * FACTOR_* authorities (e.g. FACTOR_BEARER) are added by spring security itself, not by the realm roles bridge.
     */
    private List<String> authoritiesOf(Consumer<Map<String, Object>> claims) {
        var jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("rambo")
                .claims(claims)
                .build();
        return converter.convert(jwt)
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> !authority.startsWith("FACTOR_"))
                .toList();
    }

    @Test
    void realmRoles_prefixedWithROLE() {
        assertThat(authoritiesOf(c -> c.put("realm_access", Map.of("roles", List.of("USER", "OPS")))))
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_OPS");
    }

    @Test
    void noRealmAccess_noAuthority() {
        assertThat(authoritiesOf(c -> c.put("scope", "openid")))
                .isEmpty();
    }

    @Test
    void realmAccessWithoutRoles_noAuthority() {
        assertThat(authoritiesOf(c -> c.put("realm_access", Map.of())))
                .isEmpty();
    }

    @Test
    void rolesNotAList_noAuthority() {
        assertThat(authoritiesOf(c -> c.put("realm_access", Map.of("roles", "USER"))))
                .isEmpty();
    }

    @Test
    void emptyRoles_noAuthority() {
        assertThat(authoritiesOf(c -> c.put("realm_access", Map.of("roles", List.of()))))
                .isEmpty();
    }

    /**
     * the scope claim is ignored : only realm roles feed the authorities.
     */
    @Test
    void scopeClaim_notMappedToAuthority() {
        assertThat(authoritiesOf(c -> {
            c.put("scope", "openid profile");
            c.put("realm_access", Map.of("roles", List.of("USER")));
        })).containsExactly("ROLE_USER");
    }
}
