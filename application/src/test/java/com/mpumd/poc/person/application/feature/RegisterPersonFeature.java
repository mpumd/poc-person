package com.mpumd.poc.person.application.feature;

import com.mpumd.poc.person.application.PersonApplicationService;
import com.mpumd.poc.person.application.exception.PersonAlreadyExistException;
import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.InstanceOfAssertFactories;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

class PersonPersistanceInMemory implements PersonPersistanceRepository {

    @Getter
    private List<Person> persons = new ArrayList<>();

    @Override
    public boolean isExist(PersonSearchQuery personQuery) {
        return this.persons.stream().anyMatch(p ->
                p.firstName().equals(personQuery.firstName()) &&
                        p.lastName().equals(personQuery.lastName()) &&
                        p.birthDate().equals(personQuery.birthDate())
        );
    }

    @Override
    public void push(Person person) {
        this.persons.add(person);
    }
}


@Slf4j
public class RegisterPersonFeature {

    // port-out
    PersonPersistanceInMemory personRepoInMemory;

    // port-in
    List<PersonRegistrationCommand> commands;
    List<Exception> exceptions = new ArrayList<>();

    PersonApplicationService applicationService;

    int dataSetLineNb = 0;

    @Before
    public void setup() {
        personRepoInMemory = new PersonPersistanceInMemory();
        applicationService = new PersonApplicationService(personRepoInMemory){};
    }

    @Given("I provide this following informations")
    public void createCommand(DataTable dataTable) {
        List<Map<String, String>> maps = dataTable.asMaps();
        dataSetLineNb = maps.size();
        commands = maps.stream()
                .map(map -> PersonRegistrationCommand.builder()
                        .firstName(map.get("firstName"))
                        .lastName(map.get("lastName"))
                        .birthDate(ZonedDateTime.parse(map.get("birthDate")))
                        .birthPlace(map.get("birthPlace"))
                        .gender(Gender.valueOf(map.get("gender")))
                        .nationality(Nationality.valueOf(map.get("nationality")))
                        .build())
                .toList();
    }

    @When("I engage the registration of persons")
    public void callRegisterBusinessAct() {
        for (var cmd : commands) {
            try {
                applicationService.register(cmd);
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
    }

    @Then("The persons are present inside the system")
    public void verifyPersonsAreInsideTheSystem() {
        assertThat(personRepoInMemory.persons())
                .usingRecursiveComparison()
                .comparingOnlyFields("firstName", "lastName", "birthDate", "birthPlace", "gender", "nationality")
                .isEqualTo(commands);
    }

    @Then("I receive an already exist person error for {string} {string}")
    public void verifySamePersonCantBeTwoTimeInTheSystem(String firstName, String lastName) {
        assertThat(exceptions)
                .hasSize(dataSetLineNb - 1) // 3 calls with same person, 1 correct and 2 exceptions
                .first(as(InstanceOfAssertFactories.THROWABLE))
                .isInstanceOf(PersonAlreadyExistException.class)
                .hasMessageContainingAll(firstName, lastName);
    }
}
