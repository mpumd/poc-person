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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Slf4j
public class RegisterPersonSteps {

    private PersonRegistrationCommand command;

    PersonPersistanceRepository persistanceRepository = mock(PersonPersistanceRepository.class);
    PersonApplicationService applicationService;

    {
        applicationService = new PersonApplicationService(persistanceRepository);
    }

    Person person;


    @Given("I provide this following informations")
    public void createCommand(DataTable dataTable) {
        Map<String, String> dataSet = dataTable.asMaps().getFirst();
        this.command = PersonRegistrationCommand.builder()
                .firstName(dataSet.get("firstName"))
                .lastName(dataSet.get("lastName"))
                .birthDate(ZonedDateTime.parse(dataSet.get("birthDate")))
                .birthPlace(dataSet.get("birthPlace"))
                .gender(Gender.valueOf(dataSet.get("gender")))
                .nationality(Nationality.valueOf(dataSet.get("nationality")))
                .build();


    }

    @When("I engage the registration of a person")
    public void callRegisterBusinessAct() {
        applicationService.register(command);

        var personCaptor = ArgumentCaptor.forClass(Person.class);
        verify(persistanceRepository).push(personCaptor.capture());
        this.person = personCaptor.getValue();
    }

    @Then("the person name is present inside the system")
    public void verifyPersonIsInsideTheSystem() {
        assertThat(person).isNotNull();
    }
}
