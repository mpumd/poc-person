package com.mpumd.poc.person.sb;

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
}
