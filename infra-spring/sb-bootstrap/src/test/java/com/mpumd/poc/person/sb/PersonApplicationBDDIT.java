package com.mpumd.poc.person.sb;

import io.cucumber.spring.CucumberContextConfiguration;
import lombok.Getter;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectDirectories;
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
@SelectDirectories("../../application/src/test/resources/com/mpumd/poc/person/application/feature")
// TODO restrict by glue path doesn't work here. Instead of, we have a warning
/* without a glue path, cucumber search in all the classpath and ca throw a warning wher it can't read.
WARNING: Failed to load methods of class 'net.bytebuddy.agent.VirtualMachine$ForHotSpot$Connection$ForJnaWindowsNamedPipe$Factory'.
By default Cucumber scans the entire classpath for step definitions.
You can restrict this by configuring the glue path.
...
...
...
java.lang.NoClassDefFoundError: com/sun/jna/platform/win32/Win32Exception

*/
// unfortunatly, I did find a way to indicate a package outside of the current classpath.
// @ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.mpumd.poc.person.application.feature")

@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @disabled")

// *** Cucumber Spring Configuration
// This annotations could be move outside here in a CucumberSpringConfiguration class
@CucumberContextConfiguration // pretty same behavior of @ComponentScan
@SpringBootTest(
        classes = {PersonApplicationSpringBootRunner.class},
        webEnvironment = RANDOM_PORT,
        properties = "spring.profiles.active=test") // test profile is required to track the production config
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// This class is a spring bean injectable, so you can inject the client inside features test
public class PersonApplicationBDDIT {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer dbContainer = new PostgreSQLContainer("postgres:17-alpine");

    /* DEV : activate the block to fix the port and easy use an SQL client
     * to finally inspect manually the updated schema
     */
//    static {
//        // Internal PostgreSQL port
//        dbContainer.withExposedPorts(5432);
//        // Bind host port 5432 to container port 5432
//        dbContainer.setPortBindings(List.of("5432:5432"));
//    }

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