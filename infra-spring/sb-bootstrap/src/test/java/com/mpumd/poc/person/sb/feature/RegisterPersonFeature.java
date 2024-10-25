package com.mpumd.poc.person.sb.feature;

import com.jayway.jsonpath.JsonPath;
import com.mpumd.poc.person.application.exception.PersonAlreadyExistException;
import com.mpumd.poc.person.sb.PersonApplicationBDDIT;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class RegisterPersonFeature {
    // in
    List<Map<String, String>> entriesDataSet = new ArrayList<>();
    List<String> registerPersonRestResources = new ArrayList<>();

    // out
    List<ResponseEntity<String>> okResponses = new ArrayList<>();
    List<RuntimeException> koResponses = new ArrayList<>();

    ObjectMapper mapper = new ObjectMapper();

    RestClient restClient;
    JdbcClient jdbcClient;

    RegisterPersonFeature(PersonApplicationBDDIT personApplicationBDDIT) {
        jdbcClient = personApplicationBDDIT.jdbcClient();
        restClient = personApplicationBDDIT.restClient();
    }

    @Before
    public void setUp() {
        jdbcClient.sql("DELETE FROM PERSON").update();
    }

    @Given("I provide this following informations")
    public void createCommand(DataTable dataTable) throws JsonProcessingException {
        entriesDataSet.addAll(dataTable.asMaps());
        for (var map : entriesDataSet) {
            String registerPersonJsonResource = mapper.writeValueAsString(map);
            registerPersonRestResources.add(registerPersonJsonResource);
        }
    }

    @When("I engage the registration of persons")
    public void callRegisterBusinessAct() {
        for (var resource : registerPersonRestResources) {
            try {
                ResponseEntity<String> response = restClient.post()
                        .uri("/spring/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(resource)
                        .retrieve()
                        .toEntity(String.class);
                okResponses.add(response);
            } catch (RuntimeException e) {
                koResponses.add(e);
            }
        }
    }

    @Then("The persons are present inside the system")
    public void verifyPersonsAreInsideTheSystem() {
        List<Map<String, Object>> dbDataSet = jdbcClient.sql("SELECT * FROM PERSON")
                .query()
                .listOfRows()
                .stream()
                .map(RegisterPersonFeature::normalizeKey)
                .toList();

        entriesDataSet = entriesDataSet.stream()
                .map(RegisterPersonFeature::normalizeKey)
                .toList();

        assertThat(entriesDataSet).hasSameSizeAs(dbDataSet);

        for (int i = 0; i < entriesDataSet.size(); i++) {
            var inMap = entriesDataSet.get(i);
            var dbMap = dbDataSet.get(i);

            assertNotNull(dbMap.get("id"));
            // exclude field
            dbMap.remove("id");

            assertThat(inMap)
                    .usingRecursiveComparison()
                    .isEqualTo(dbMap);
        }
    }

    @Then("I receive an already exist person error for {string} {string} {int} times")
    public void verifySamePersonCantBeTwoTimeInTheSystem(String firstName, String lastName, int errorNb) {
        assertThat(koResponses).hasSize(errorNb).allSatisfy(exception -> {
            assertThat(exception).isInstanceOf(HttpClientErrorException.class);
            var msg = exception.getMessage();
            String jsonPart = msg.substring(msg.indexOf("{"), msg.lastIndexOf("}") + 1);

            assertEquals(
                    JsonPath.read(jsonPart, "$.status").toString(),
                    "409");
            assertEquals(
                    JsonPath.read(jsonPart, "$.title").toString(),
                    PersonAlreadyExistException.class.getSimpleName());
            assertEquals(
                    JsonPath.read(jsonPart, "$.detail").toString(),
                    "%s %s already exist !".formatted(firstName, lastName));
        });
    }

    private static <V> Map<String, V> normalizeKey(Map<String, V> map) {
        var newMap = new HashMap<String, V>();
        for (Entry<String, V> entry : map.entrySet()) {
            var newKey = entry.getKey().replace("_", "").toLowerCase();
            newMap.put(newKey, entry.getValue());
        }
        return newMap;
    }
}
