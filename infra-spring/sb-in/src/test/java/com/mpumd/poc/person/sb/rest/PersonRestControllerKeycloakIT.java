package com.mpumd.poc.person.sb.rest;

import com.mpumd.poc.person.sb.application.PersonAppSvc;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.UUID;

import static io.restassured.http.ContentType.JSON;
import static io.restassured.http.ContentType.URLENC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("keycloak")
class PersonRestControllerKeycloakIT {

    static final String REALM = "poc-person";
    static final String CLIENT_ID = "poc-person-app";
    // users declared in the realm import, with their realm roles
    static final String ADMIN_USER = "rambo-admin";
    static final String SIMPLE_USER = "rambo-user";
    static final String PASSWORD = "s3cr3t";

    /**
     * Reused across runs, so the ~55 s startup is paid only once : the container
     * is voluntarily never stopped, which is what makes the reuse possible.
     * Needs {@code testcontainers.reuse.enable=true} in ~/.testcontainers.properties,
     * otherwise testcontainers falls back on a fresh container per run.
     */
    static final KeycloakContainer KEYCLOAK = new KeycloakContainer("quay.io/keycloak/keycloak:26.0")
            // the realm is the contract : same client and roles as prod, plus the
            // test users. Changing it makes testcontainers start a fresh container.
            .withRealmImportFile("/keycloak/poc-person-realm.json")
            .withReuse(true);

    static {
        KEYCLOAK.start();
    }

    @DynamicPropertySource
    static void keycloakIssuer(DynamicPropertyRegistry registry) {
        // the port is drawn at startup, the issuer can only be known here
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> KEYCLOAK.getAuthServerUrl() + "/realms/" + REALM
        );
    }

    // the authorization rules themselves belong to the application service and
    // are covered by PersonAppSvcSecurityAuthorisationITest, here it is only
    // mocked : what it sees, and what the adapter does with its refusal
    @MockitoBean
    PersonAppSvc appService;

    @LocalServerPort
    int port;

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

    /**
     * The contract between the authority and the application service : the roles
     * held by keycloak must reach the security context as spring authorities,
     * otherwise every hasRole() would deny.
     */
    @Test
    void registerAuthorizedWithRightRolesForwarded() {
        var id = UUID.randomUUID();
        var authorities = new ArrayList<String>();
        given(appService.register(any())).willAnswer(_ -> {
            SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .forEach(authority -> authorities.add(authority.getAuthority()));
            return id;
        });

        RestAssured.given()
                .port(port)
                .auth().oauth2(postUserAndGetAccessToken(ADMIN_USER))
                .contentType(JSON)
                .body(registerPayload)
                .when()
                .post("/person")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", equalTo(id.toString()));

        assertThat(authorities).contains("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void register401WithoutAuthentication() {
        RestAssured.given()
                // .auth().oauth2(postUserAndGetAccessToken(ADMIN_USER))
                .contentType(JSON)
                .body(registerPayload)
                .port(port)
                .when()
                .post("/person")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());

        verifyNoInteractions(appService);
    }

    @Test
    void register403_AccessDeniedException() {
        given(appService.register(any())).willThrow(new AccessDeniedException("Access Denied"));

        RestAssured.given()
                .auth().oauth2(postUserAndGetAccessToken(SIMPLE_USER))
                .contentType(JSON)
                .body(registerPayload)
                .port(port)
                .when()
                .post("/person")
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    // ##### keycloak driving #####

    /**
     * Direct access grant : users and roles are declared in the realm import, so
     * getting a token for one of them is a plain form post, no admin api needed.
     */
    static String postUserAndGetAccessToken(String username) {
        String token = RestAssured.given()
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
                .extract()
                .path("access_token");

        assertThat(token).isNotEmpty();
        return token;
    }
}
