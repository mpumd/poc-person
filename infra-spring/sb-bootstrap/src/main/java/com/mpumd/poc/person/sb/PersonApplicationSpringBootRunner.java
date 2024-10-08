package com.mpumd.poc.person.sb;

import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableAdminServer
public class PersonApplicationSpringBootRunner {

    public static void main(String[] args) {
        SpringApplication.run(PersonApplicationSpringBootRunner.class, args);
    }


    // just for the test at this moment
    @Bean
    public PersonPersistanceRepository buildPersonPersistanceInMemory() {
        return new PersonPersistanceRepository() {

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
        };
    }
}
