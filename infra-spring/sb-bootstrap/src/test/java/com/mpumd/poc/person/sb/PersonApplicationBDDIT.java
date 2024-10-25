package com.mpumd.poc.person.sb;

import io.cucumber.spring.CucumberContextConfiguration;
import lombok.Getter;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectFile;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.cucumber.core.options.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

// *** Cucumber Junit Runner
@Suite
@IncludeEngines("cucumber")
@SelectFile("../../application/src/test/resources/com/mpumd/poc/person/application/feature/registerPerson.feature")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @disabled")
// root package in glue property to target the CucumberContextConfiguration annotation
// @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.mpumd.poc.person.sb")

// *** Cucumber Spring Configuration
// This annotations could be move outside here in a CucumberSpringConfiguration class
@CucumberContextConfiguration // pretty same behavior of @ComponentScan
@SpringBootTest(
        classes = {PersonApplicationSpringBootRunner.class},
        webEnvironment = RANDOM_PORT,
        properties = "spring.profiles.active=test") // test profile is required to track the production config
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// This class is injectable
public class PersonApplicationBDDIT {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer dbContainer = new PostgreSQLContainer("postgres:17-alpine");

    @Getter
    private JdbcClient jdbcClient;
    @Getter
    private RestClient restClient;

    /* static { dbContainer.start(); } */



    PersonApplicationBDDIT(@LocalServerPort int port) {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        jdbcClient = JdbcClient.create(new DriverManagerDataSource(
                dbContainer.getJdbcUrl(),
                dbContainer.getUsername(),
                dbContainer.getPassword()
        ));
    }

    @io.cucumber.java.BeforeAll
    public static void setupAll() {
        dbContainer.start();
    }

    @io.cucumber.java.AfterAll
    public static void afterAll() {
        dbContainer.stop();
    }
}