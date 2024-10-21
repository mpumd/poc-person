package com.mpumd.poc.person.sb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@Slf4j
// FIXME run with cucumber feature here
// FIXME run with native image. two different test I guest
public class PocPersonITest {

//    @LocalServerPort
//    private int port;

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:17-alpine");


    @Autowired
    private TestRestTemplate restTemplate;

//    @Test
//    void loadContext() { }

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testGetEndpoint() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonBody = """
            {
              "firstName": "John",
              "lastName": "Rambo",
              "gender": "Male",
              "birthDate": "1947-07-06T00:00:00Z",
              "birthPlace": "Bowie, Arizona, USA",
              "nationality": "US"
            }
            """;

        ResponseEntity<String> response = restTemplate.exchange(
                "/person",
                HttpMethod.POST,
                new HttpEntity<>(jsonBody, headers),
                String.class
        );


        assertEquals(201, response.getStatusCode().value());
        var uuidStr = mapper.readTree(response.getBody()).get("id").asText();
        assertDoesNotThrow(() -> UUID.fromString(uuidStr));
    }

    //    @Container
//    public static GenericContainer<?> app = new GenericContainer<>("poc-person:latest")
//            .withExposedPorts(8080);
//
//    @DynamicPropertySource
//    static void properties(DynamicPropertyRegistry registry) {
//        registry.add("app.container.port", () -> app.getMappedPort(8080));
//    }

//    @Test
//    public void testNativeDockerImage() {
//        String baseUrl = "http://" + app.getHost() + ":" + app.getMappedPort(8080);
//        TestRestTemplate restTemplate = new TestRestTemplate();
//
//        String response = restTemplate.getForObject(baseUrl + "/api/hello", String.class);
//        assertEquals("Hello from Docker native image!", response);
//    }
}