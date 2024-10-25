package com.mpumd.poc.person.sb.feature;

import com.mpumd.poc.person.sb.PersonApplicationSpringBootRunner;
import io.cucumber.java.AfterAll;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@CucumberContextConfiguration
@SpringBootTest(classes = {PersonApplicationSpringBootRunner.class}, webEnvironment = RANDOM_PORT,
        properties = {
                "spring.jackson.deserialization.adjust-dates-to-context-time-zone=false",
                "spring.jpa.properties.hibernate.type.descriptor.sql.ZonedDateTime=string"
        })
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class BBDTestConfiguration {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer dbContainer = new PostgreSQLContainer("postgres:17-alpine");

    static {
        // TODO why start the container too early ?
        // Cucumber isn't capable to figure out the time to start ? or junit ?
        // SB and cucumber doesn't synchronize themself ccrrectly
//        dbContainer.withCommand("postgres -c timezone=UTC");
        dbContainer.start();
    }

    @AfterAll
    static void afterAll() {
        dbContainer.stop();
    }
}
