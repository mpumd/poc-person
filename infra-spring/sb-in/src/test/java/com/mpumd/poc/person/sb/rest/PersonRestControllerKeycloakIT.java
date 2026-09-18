package com.mpumd.poc.person.sb.rest;

import com.mpumd.poc.person.sb.application.PersonAppSvc;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.URLENC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * The whole authentication chain against a real authority : keycloak hosts the
 * users and their roles, signs the jwt, and the api only validates it.
 * <p>
 * The out adapter is mocked : what is under test is
 * http request -> jwt validation -> authorities mapping -> @PreAuthorize.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "springdoc.api-docs.path=/api-docs",
                "springdoc.swagger-ui.path=/swagger-ui"
        })
@ActiveProfiles("keycloak")
// PER_CLASS so the route table can feed a non-static @MethodSource
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonRestControllerKeycloakIT {

    static final String REALM = "poc-person";
    static final String CLIENT_ID = "poc-person-app";
    // synthetic identity declared in the realm import : role nature irrelevant, only proves access passes
    static final String GRANTED_USER = "rambo-granted";
    static final String PASSWORD = "s3cr3t";

    /**
     * Reused across runs and modules when testcontainers.reuse.enable=true (~/.testcontainers.properties),
     * otherwise one container per JVM, torn down by Ryuk.
     * Shared with sb-bootstrap only if image and realm file stay byte identical : they feed the reuse hash.
     */
    static final KeycloakContainer KEYCLOAK = new KeycloakContainer("quay.io/keycloak/keycloak:26.0")
            // the realm is the contract : same client and roles as prod, plus the test users.
            .withRealmImportFile("/keycloak/poc-person-realm.json")
            .withReuse(true);

    static {
        KEYCLOAK.start();
    }

    @DynamicPropertySource
    static void keycloakIssuer(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> ISSUER);
    }

    // the port is drawn at startup, the issuer can only be known once the container runs
    static final String ISSUER = KEYCLOAK.getAuthServerUrl() + "/realms/" + REALM;
    static final String UUID_FORMAT = "[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}";

    @MockitoBean
    PersonAppSvc appService;

    @LocalServerPort
    int port;

    // the prod beans doing jwt validation and the realm-role -> authority mapping
    @Autowired
    JwtDecoder jwtDecoder;
    @Autowired
    JwtAuthenticationConverter jwtAuthenticationConverter;

    // the live route table : every business endpoint spring actually exposes
    @Autowired
    RequestMappingHandlerMapping requestMappingHandlerMapping;

    String registerPayload = """
            {
              "firstName": "John",
              "lastName": "Rambo",
              "gender": "Male",
              "birthDate": "1947-07-06T05:00:00+01:00",
              "birthPlace": "Bowie, Arizona, USA",
              "nationality": "US"
            }
            """;

    record Route(String httpMethod, String path) {
        @Override
        public String toString() {
            return httpMethod + " " + path;
        }
    }

    @ParameterizedTest(name = "401 without token on {0}")
    @MethodSource("businessRoutes")
    void everyBusinessRouteRejectsUnauthenticated(Route route) {
        call(RestAssured.given(), route)
                // No token, and nothing must reach the app service.
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

        verifyNoInteractions(appService);
    }

    /** the functional outcome (400/415, no payload sent) is out of scope, only 401/403 would be a security failure. */
    @ParameterizedTest(name = "granted user passes on {0}")
    @MethodSource("businessRoutes")
    void everyBusinessRoutePassesForGrantedUser(Route route) {
        call(RestAssured.given().auth().oauth2(accessTokenOf(GRANTED_USER)), route)
                .then()
                .statusCode(not(HttpStatus.UNAUTHORIZED.value()))
                .statusCode(not(HttpStatus.FORBIDDEN.value()));
    }

    /**
     * The token keycloak really signs, the contract this api relies on : an access token
     * of our client, issued by our realm, carrying the realm roles and the identity.
     * Claims drawn at each call are asserted on their format only.
     */
    @Test
    void accessToken_carriesTheExpectedClaims() {
        var jwt = jwtDecoder.decode(accessTokenOf(GRANTED_USER));

        assertThat(jwt.getClaims()).containsOnlyKeys(
                "typ", "iss", "azp", "acr", "realm_access", "scope",           // authentication and authorization
                "exp", "iat", "jti", "sid", "sub",                             // drawn at each call
                "preferred_username", "email", "email_verified",               // identity, from the realm import
                "name", "given_name", "family_name");

        assertThat(jwt.getClaimAsString("typ")).isEqualTo("Bearer");  // an access token, not the id token
        assertThat(jwt.getIssuer()).hasToString(ISSUER);                       // the realm that signed it
        assertThat(jwt.getClaimAsString("azp")).isEqualTo(CLIENT_ID);          // the client it was issued for
        assertThat(jwt.getClaimAsString("acr")).isEqualTo("1");       // authentication level, 1 = password
        assertThat(jwt.getClaimAsMap("realm_access"))
                .containsEntry("roles", List.of("GRANTED"));               // what becomes ROLE_* for hasRole()
        assertThat(jwt.getClaimAsString("scope").split(" "))
                .containsExactlyInAnyOrder("email", "profile");        // granted scopes, order not stable

        assertThat(jwt.getClaimAsString("sub")).matches(UUID_FORMAT);          // user technical id
        assertThat(jwt.getClaimAsString("jti")).matches(UUID_FORMAT);          // token id
        assertThat(jwt.getClaimAsString("sid")).matches(UUID_FORMAT);          // keycloak session id
        assertThat(jwt.getIssuedAt()).isBefore(Instant.now());                 // iat
        assertThat(jwt.getExpiresAt()).isAfter(Instant.now());                 // exp, the 5 min bearer window

        assertThat(jwt.getClaimAsString("preferred_username")).isEqualTo(GRANTED_USER);
        assertThat(jwt.getClaimAsString("email")).isEqualTo(GRANTED_USER + "@poc.local");
        assertThat(jwt.getClaimAsBoolean("email_verified")).isTrue();
        assertThat(jwt.getClaimAsString("name")).isEqualTo("John Rambo");
        assertThat(jwt.getClaimAsString("given_name")).isEqualTo("John");
        assertThat(jwt.getClaimAsString("family_name")).isEqualTo("Rambo");
    }

    /** The keycloak realm role becomes a spring authority : the bridge every hasRole() relies on. */
    @Test
    void realmRole_convertedToSpringAuthority() {
        var token = accessTokenOf(GRANTED_USER);
        var authentication = jwtAuthenticationConverter.convert(jwtDecoder.decode(token));
        assertThat(authentication.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_GRANTED");
    }

    @Test
    void accessDeniedFromServiceBecomes403() {
        given(appService.register(any())).willThrow(new AccessDeniedException("Access Denied"));

        RestAssured.given()
                .auth().oauth2(accessTokenOf(GRANTED_USER))
                .contentType(JSON)
                .body(registerPayload)
                .port(port)
                .when()
                .post("/person")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
    // disable in PROD

    @ParameterizedTest
    @ValueSource(strings = {
            "/api-docs",
            "/swagger-ui/index.html"
    })
    void permitAllForDevelopersOrIADocs(String url) {
        RestAssured.given()
                .port(port)
                .when()
                .get(url)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * The business routes, read from the live route table : any @RestController
     * added in our package is covered automatically, none can silently escape
     * the security tests. Springdoc, actuator and framework handlers are dropped
     * by keeping only beans of our package.
     */
    Stream<Route> businessRoutes() {
        return requestMappingHandlerMapping.getHandlerMethods()
                .entrySet()
                .stream()
                .filter(e -> e.getValue().getBeanType().getPackageName().startsWith("com.mpumd.poc.person"))
                .flatMap(e -> {
                    var patterns = e.getKey().getPathPatternsCondition().getPatterns();
                    var methods = e.getKey().getMethodsCondition().getMethods();
                    return patterns.stream().flatMap(pattern -> methods
                            .stream()
                            .map(method -> new Route(method.name(), pattern.getPatternString())));
                })
                .distinct();
    }

    /** Fires the request on the route, path variables filled with a throwaway value (irrelevant : security runs before binding). */
    Response call(RequestSpecification spec, Route route) {
        var path = route.path().replaceAll("\\{[^}]+}", UUID.randomUUID().toString());
        return spec.port(port).when().request(route.httpMethod(), path);
    }

    /**
     * users and roles come from the realm import, so a token
     * is a plain form post, no admin api needed.
     */
    static String accessTokenOf(String username) {
        return RestAssured.given()
                .baseUri(KEYCLOAK.getAuthServerUrl())
                .contentType(URLENC)
                .formParam("client_id", CLIENT_ID)
                .formParam("username", username)
                .formParam("password", PASSWORD)
                .formParam("grant_type", "password")
                .when()
                .post("/realms/{realm}/protocol/openid-connect/token", REALM)
                .then()
                .statusCode(HttpStatus.OK.value())
                // assert token
                .body("access_token", not(emptyOrNullString()))
                .extract()
                .path("access_token");
    }
}
