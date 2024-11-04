package com.mpumd.poc.person.application.feature;

import com.mpumd.poc.person.application.PersonApplicationService;
import com.mpumd.poc.person.application.PersonPersistanceInMemory;
import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
public class ChangeSexFeature {
    // port-in
    private Map<String, Object> cmd = new HashMap<>();

    // port-out
    private PersonPersistanceInMemory personRepoInMemory;

    private PersonApplicationService applicationService;

    private LocalDateTime changeGenderDate;
    private Gender newGender;

    private List<Exception> exceptions = new ArrayList<>();

    @Before
    public void setup() {
        personRepoInMemory = new PersonPersistanceInMemory();
        applicationService = new PersonApplicationService(personRepoInMemory) {
        };
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

    private void savePerson() {
        applicationService.register(PersonRegistrationCommand.builder()
                .firstName((String) cmd.get("firstname"))
                .lastName((String) cmd.get("lastname"))
                .birthDate((ZonedDateTime) cmd.get("birthdate"))
                .birthPlace((String) cmd.get("birthplace"))
                .gender((Gender) cmd.get("gender"))
                .nationality((Nationality) cmd.get("nationality"))
                .build());

        assertThat(personRepoInMemory.persons()).hasSize(1);
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
            applicationService.changeSex(
                    personRepoInMemory.persons().get(0).id(),
                    newGender,
                    changeGenderDate
            );
        } catch (Exception ex) {
            exceptions.add(ex);
        }
    }

    @Then("I see the history of gender like bellow in the order")
    public void checkDataInTheSystem(DataTable dataTable) {
        var genderMap = dataTable.asMaps()
                .stream()
                .collect(Collectors.toMap(
                        map -> LocalDateTime.parse(map.get("changeDate")),
                        map -> Gender.valueOfName(map.get("gender")),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        assertThat(personRepoInMemory.persons().get(0))
                .extracting("genderChangeHistory")
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .containsExactlyEntriesOf(genderMap);
    }

    @Then("The system refuse to change the gender with the following message {string}")
    public void checkExForTheSystem(String message) {
        assertThat(exceptions)
                .hasSize(1)
                .first()
                .asInstanceOf(InstanceOfAssertFactories.THROWABLE)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);

    }
//    @Then("The system refuse to change the gender with the following message {string}")
//    public void checkExIfTheGenderIsSame(String message) {
//        assertThat(exceptions)
//                .hasSize(1)
//                .first()
//                .asInstanceOf(InstanceOfAssertFactories.THROWABLE)
//                .isInstanceOf(IllegalArgumentException.class)
//                .hasMessage(message);
//
//    }
}