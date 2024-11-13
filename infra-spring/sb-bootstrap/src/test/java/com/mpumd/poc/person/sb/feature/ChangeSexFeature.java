package com.mpumd.poc.person.sb.feature;

import com.jayway.jsonpath.JsonPath;
import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.sb.PersonApplicationBDDIT;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Slf4j
public class ChangeSexFeature {
    // port-in
    private Map<String, Object> cmd = new HashMap<>();

    private LocalDateTime changeGenderDate;
    private Gender newGender;

    private List<Exception> exceptions = new ArrayList<>();

    private RestClient restClient;
    private JdbcClient jdbcClient;

    private String currentPersonId = "";

    ChangeSexFeature(PersonApplicationBDDIT personApplicationBDDIT) {
        jdbcClient = personApplicationBDDIT.jdbcClient();
        restClient = personApplicationBDDIT.restClient();
    }

    @Before
    public void setUp() {
        jdbcClient.sql("DELETE FROM PERSON").update();
    }

    @Given("I have an existing person whose firstname is {string},")
    public void setFirstName(String firstName) {
        assertThat(firstName).isNotBlank();
        cmd.put("firstname", firstName);
    }

    @And("lastname is {string},")
    public void setLastName(String lastName) {
        assertThat(lastName).isNotBlank();
        cmd.put("lastname", lastName);
    }

    @And("birthdate is {string},")
    public void setBirthDate(String birthDate) {
        assertThat(birthDate).isNotBlank();
        cmd.put("birthdate", ZonedDateTime.parse(birthDate));
    }

    @And("birthplace is {string},")
    public void setBirthPlace(String birthPlace) {
        assertThat(birthPlace).isNotBlank();
        cmd.put("birthplace", birthPlace);
    }

    @And("gender is {string},")
    public void setGender(String gender) {
        assertThat(gender).isNotBlank();
        cmd.put("gender", Gender.valueOfName(gender));
    }

    @And("nationality is {string}")
    public void setNationality(String nationality) {
        assertThat(nationality).isNotBlank();
        cmd.put("nationality", Nationality.valueOfName(nationality));
        savePerson();
    }

    @SneakyThrows(IOException.class) // jackson
    private void savePerson() {
        String registerPayload = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "gender": "%s",
                  "birthDate": "%s",
                  "birthPlace": "%s",
                  "nationality": "%s"
                }
                """.formatted(
                cmd.get("firstname"),
                cmd.get("lastname"),
                cmd.get("gender"),
                cmd.get("birthdate"),
                cmd.get("birthplace"),
                cmd.get("nationality")
        );

        String jsonResponse = restClient.post()
                .uri("/spring/person")
                .contentType(MediaType.APPLICATION_JSON)
                .body(registerPayload)
                .retrieve()
                .toEntity(String.class)
                .getBody();
        currentPersonId = new ObjectMapper().readTree(jsonResponse).get("id").asText();

        assertThat(jdbcClient.sql("SELECT id FROM PERSON p").query().listOfRows())
                .hasSize(1)
                .first()
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsEntry("id", UUID.fromString(currentPersonId));
    }

    @Given("I give the current date {string}, and the gender {string} and an uuid")
    public void givenNewGenderData(String date, String gender) {
        assertThat(date).isNotBlank();
        assertThat(gender).isNotBlank();
        changeGenderDate = LocalDateTime.parse(date);
        newGender = Gender.valueOfName(gender);
    }

    @When("I engage the changeSex business act")
    public void callChangeSex() {
        try {
            restClient.post()
                    .uri("/spring/person/" + currentPersonId + "/gender")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("""
                            {
                                "gender" : "%s",
                                "changeDate" : "%s"
                            }
                            """.formatted(this.newGender, this.changeGenderDate))
                    .retrieve()
                    .body(String.class);
        } catch (Exception ex) {
            exceptions.add(ex);
        }
    }

    @Then("I see the history of gender like bellow in the order")
    public void checkDataInTheSystem(DataTable dataTable) {
        Map<LocalDateTime, Gender> bddDataMap = dataTable.asMaps()
                .stream()
                .collect(Collectors.toMap(
                        map -> LocalDateTime.parse(map.get("changeDate")),
                        map -> Gender.valueOfName(map.get("gender")),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        Map<LocalDateTime, Gender> actual = jdbcClient.sql("""
                        SELECT g.gender, g.change_date
                        FROM person p
                        INNER JOIN Genders g ON p.id = g.person_id
                        """)
                .query()
                .listOfRows()
                .stream()
                .collect(Collectors.toMap(
                        map -> java.sql.Timestamp.class.cast(map.get("change_date")).toLocalDateTime(),
                        map -> Gender.valueOfName((String) map.get("gender")),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        assertThat(actual).isEqualTo(bddDataMap);

    }

    @Then("The system refuse to change the gender with the following message {string}")
    public void checkExForTheSystem(String message) {
        assertThat(exceptions).hasSize(1).allSatisfy(exception -> {
            assertThat(exception).isInstanceOf(HttpClientErrorException.class);
            var msg = exception.getMessage();
            String jsonPart = msg.substring(msg.indexOf("{"), msg.lastIndexOf("}") + 1);

            assertEquals(JsonPath.read(jsonPart, "$.status").toString(),
                    "400");
            assertEquals(JsonPath.read(jsonPart, "$.title").toString(),
                    IllegalArgumentException.class.getSimpleName());
            assertEquals(JsonPath.read(jsonPart, "$.detail").toString(),
                    message);
        });

    }

}