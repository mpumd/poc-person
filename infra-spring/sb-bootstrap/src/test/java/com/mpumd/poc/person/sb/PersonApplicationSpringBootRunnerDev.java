package com.mpumd.poc.person.sb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

// for intellij local runner
public class PersonApplicationSpringBootRunnerDev {
    public static void main(String[] args) throws IOException, URISyntaxException {
        final var uri = Thread.currentThread().getContextClassLoader().getResource("").toURI();
        final var dockerComposePath = Path.of(uri)
                .resolve("../../")
                .normalize()
                .toRealPath()
                .toString();

        PersonApplicationSpringBootRunner.main(new String[]{
                "--spring.profiles.active=dev",
                "--spring.docker.compose.file=" + dockerComposePath + "/compose.yaml"
        });
    }


    // Run at startup
    @Component
    @Slf4j
    static class StartupListener implements CommandLineRunner {

        RestClient restClient;

        public StartupListener(
                @Value("${server.port}") int port,
                @Value("${server.servlet.context-path}") String contextPath) {
            restClient = RestClient.builder()
                    .baseUrl("http://localhost:%s%s".formatted(port, contextPath))
                    .build();
        }

        @Override
        public void run(String... args) {
            try {
                restClient.post()
                        .uri("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {
                                  "firstName": "John",
                                  "lastName": "Rambo",
                                  "gender": "Male",
                                  "birthDate": "1947-07-06T05:00:00+01:00",
                                  "birthPlace": "Bowie, Arizona, USA",
                                  "nationality": "US"
                                }
                                """)
                        .retrieve()
                        .toEntity(String.class);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }
}
