package com.mpumd.poc.person.context.feature;

import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Map;

@Slf4j
public class RegisterPersonSteps {

    private PersonRegistrationCommand command;
    private Exception exception;

    @Given("I want to register a new person")
    public void init() {
        log.info("init step....................");
    }

    @When("I provide this following informations")
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

    @When("and step juste for the fun")
    public void callRegisterBusinessAct(String email) {
        try {

        } catch (Exception e) {
            this.exception = e;
        }
    }

    @When("the person name is present inside the system")
    public void verifyPersonIsInsideTheSystem(String birthDate) {
        // Stocke la date de naissance pour utilisation ultérieure
    }

    @And("we have no error message during the creation")
    public void verifyNoError() {
    }

}
