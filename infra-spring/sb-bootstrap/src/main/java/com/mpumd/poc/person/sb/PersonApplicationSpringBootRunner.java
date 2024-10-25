package com.mpumd.poc.person.sb;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class PersonApplicationSpringBootRunner {
    public static void main(String[] args) {
        SpringApplication.run(PersonApplicationSpringBootRunner.class, args);
    }
}
