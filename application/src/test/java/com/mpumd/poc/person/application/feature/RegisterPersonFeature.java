package com.mpumd.poc.person.application.feature;

import com.mpumd.poc.person.application.PersonApplicationService;
import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.mockito.ArgumentCaptor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Slf4j
public class RegisterPersonFeature {

    PersonPersistanceRepository persistanceRepository = mock(PersonPersistanceRepository.class);
    PersonApplicationService applicationService;

    {
        applicationService = new PersonApplicationService(persistanceRepository);
    }


    List<Map<String, String>> dataSet;
    List<PersonRegistrationCommand> commands;
    List<Person> persons;


    @Given("I provide this following informations")
    public void createCommand(DataTable dataTable) {
        dataSet = dataTable.asMaps();
        commands = dataSet.stream()
                .map(dataSet -> PersonRegistrationCommand.builder()
                        .firstName(dataSet.get("firstName"))
                        .lastName(dataSet.get("lastName"))
                        .birthDate(ZonedDateTime.parse(dataSet.get("birthDate")))
                        .birthPlace(dataSet.get("birthPlace"))
                        .gender(Gender.valueOf(dataSet.get("gender")))
                        .nationality(Nationality.valueOf(dataSet.get("nationality")))
                        .build())
                .toList();
    }

    @When("I engage the registration of a person")
    public void callRegisterBusinessAct() {
        commands.forEach(applicationService::register);
        log.info("### call PersonApplicationService#register");

        var personCaptor = ArgumentCaptor.forClass(Person.class);
        verify(persistanceRepository, times(commands.size())).push(personCaptor.capture());
        this.persons = personCaptor.getAllValues();
    }

    @Then("The persons are present inside the system")
    public void verifyPersonIsInsideTheSystem() {
        assertThat(persons)
                .usingRecursiveComparison()
                .comparingOnlyFields("firstName", "lastName", "birthDate", "birthPlace", "gender", "nationality")
                .isEqualTo(commands);
    }
}
