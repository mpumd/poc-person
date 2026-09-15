package com.mpumd.poc.person.sb;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSender;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.stream.Stream;

import static io.restassured.http.ContentType.URLENC;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

/**
 * Validates the ops surface authorization of the assembled application against a
 * real keycloak : the actuator endpoints and the SB admin ui, which only exist
 * once bootstrap wires them. The whole ops part of {@code KeycloakSecurityConfig}
 * is exercised end to end : liveness / readiness open to anyone, every other
 * actuator endpoint and the admin ui reserved to the OPS role.
 * <p>
 * The rest facing part of the same config (user authentication, role forwarding,
 * swagger / api-docs exposure) is covered closer to the adapter by
 * {@code PersonRestControllerKeycloakIT} in sb-in.
 * <p>
 * The out side needs a datasource (jpa validate + liquibase run on boot), hence
 * the postgres container ; the authority is the same reusable keycloak.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.profiles.active=keycloak"
)
@Testcontainers(disabledWithoutDocker = true)
class ActuatorAndAdminSecurityKeycloakIT {

    static final String REALM = "poc-person";
    static final String CLIENT_ID = "poc-person-app";
    static final String OPS_USER = "rambo-ops";
    static final String CLASSIC_USER = "rambo-user";
    static final String PASSWORD = "s3cr3t";

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> DB = new PostgreSQLContainer<>("postgres:17-alpine");

    /**
     * Reused across runs and modules when testcontainers.reuse.enable=true (~/.testcontainers.properties),
     * otherwise one container per JVM, torn down by Ryuk.
     * Shared with sb-in only if image and realm file stay byte identical : they feed the reuse hash.
     */
    static final KeycloakContainer KEYCLOAK = new KeycloakContainer("quay.io/keycloak/keycloak:26.0")
            .withRealmImportFile("/keycloak/poc-person-realm.json")
            .withReuse(true);

    static {
        KEYCLOAK.start();
    }

    @DynamicPropertySource
    static void keycloakIssuer(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> KEYCLOAK.getAuthServerUrl() + "/realms/" + REALM
        );
    }

    @LocalServerPort
    int port;

    // actuator and admin are served under the servlet context path (same port)
    @Value("${server.servlet.context-path}")
    String contextPath;

    // open to anyone, the kubelet / LB has no user
    @ParameterizedTest
    @ValueSource(strings = {
            "/actuator/health/liveness",
            "/actuator/health/readiness"
    })
    void probeIsOpenToAnyone(String path) {
        RESTCallAnonymous().get(contextPath + path)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    /**
     * Every endpoint the config gates behind the OPS role : the whole actuator
     * surface but the probes, plus the SB admin ui which is a graphical window on
     * the very same data. All of them return 200 once an OPS reaches them.
     */
    static Stream<String> getRoleOPSEndpoints() {
        return Stream.of(
                "/actuator/health",
                "/actuator/info",
                "/actuator/metrics",
                "/actuator/env",
                "/actuator/beans",
                "/actuator/configprops",
                "/actuator/conditions",
                "/actuator/mappings",
                "/actuator/loggers",
                "/actuator/threaddump",
                "/actuator/scheduledtasks",
                "/actuator/liquibase",
                "/admin/applications"
        );
    }

    @ParameterizedTest
    @MethodSource("getRoleOPSEndpoints")
    void ROLE_OPS_401_Anonymous(String path) {
        RESTCallAnonymous().get(contextPath + path)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @ParameterizedTest
    @MethodSource("getRoleOPSEndpoints")
    void ROLE_OPS_403_commonUser(String path) {
        RESTCallAuthenticatedAs(CLASSIC_USER)
                .get(contextPath + path)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @ParameterizedTest
    @MethodSource("getRoleOPSEndpoints")
    void ROLE_OPS_200_OPS_USER(String path) {
        RESTCallAuthenticatedAs(OPS_USER).get(contextPath + path)
                .then().statusCode(HttpStatus.OK.value());
    }

    private RequestSender RESTCallAnonymous() {
        return RestAssured.given()
                .port(port)
                .when();
    }

    private RequestSender RESTCallAuthenticatedAs(String username) {
        return RestAssured.given()
                .port(port)
                .auth()
                .oauth2(putUserAndGetToken(username))
                .when();
    }

    /**
     * users and roles come from the realm import, so a token
     * is a plain form post, no admin api needed.
     */
    static String putUserAndGetToken(String username) {
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
