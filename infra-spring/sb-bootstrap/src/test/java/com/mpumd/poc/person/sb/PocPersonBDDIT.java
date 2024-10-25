package com.mpumd.poc.person.sb;

import lombok.extern.slf4j.Slf4j;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectFile;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectFile("../../application/src/test/resources/com/mpumd/poc/person/application/feature/registerPerson.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @disabled")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.mpumd.poc.person.sb.feature")
@Slf4j
public class PocPersonBDDIT {


//    ObjectMapper mapper = new ObjectMapper();

//    @Test
//    public void testGetEndpoint() throws JsonProcessingException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        String jsonBody = """
//            {
//              "firstName": "John",
//              "lastName": "Rambo",
//              "gender": "Male",
//              "birthDate": "1947-07-06T00:00:00Z",
//              "birthPlace": "Bowie, Arizona, USA",
//              "nationality": "US"
//            }
//            """;
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                "/person",
//                HttpMethod.POST,
//                new HttpEntity<>(jsonBody, headers),
//                String.class
//        );
//
//
//        assertEquals(201, response.getStatusCode().value());
//        var uuidStr = mapper.readTree(response.getBody()).get("id").asText();
//        assertDoesNotThrow(() -> UUID.fromString(uuidStr));
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