package com.mpumd.poc.person.application;

import com.mpumd.poc.person.application.exception.PersonAlreadyExistException;
import com.mpumd.poc.person.context.PersonPersistanceRepository;
import com.mpumd.poc.person.context.aggregat.Gender;
import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.command.PersonRegistrationCommand;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonApplicationServiceTest {

    @Mock
    Person person;
    @Mock
    ZonedDateTime birthDate;
    @Mock
    Gender gender;
    @Mock
    Nationality nationality;

    @Mock
    PersonPersistanceRepository personPersistanceRepository;

    @InjectMocks
    PersonApplicationService personApplicationService;

    PersonRegistrationCommand command;

    @BeforeEach
    void setUp() {
        command = PersonRegistrationCommand.builder()
                .firstName("mpu")
                .lastName("md")
                .birthDate(birthDate)
                .birthPlace("anywhere")
                .gender(gender)
                .nationality(nationality)
                .build();
    }

    @Test
    void registerNewPerson() {
        var queryCaptor = ArgumentCaptor.forClass(PersonSearchQuery.class);
        given(personPersistanceRepository.isExist(queryCaptor.capture())).willReturn(false);

        try (var mockedStatic = mockStatic(Person.class)) {

            mockedStatic.when(() -> Person.register(command)).thenReturn(person);

            personApplicationService.register(command);

            verify(personPersistanceRepository).push(person);
        }
    }

    @Test
    void registerThrowAlreadyExistEx() {
        var queryCaptor = ArgumentCaptor.forClass(PersonSearchQuery.class);
        given(personPersistanceRepository.isExist(queryCaptor.capture())).willReturn(true);

        try (var mockedStatic = mockStatic(Person.class)) {
            // when
            assertThatThrownBy(() -> personApplicationService.register(command))
                    .isInstanceOf(PersonAlreadyExistException.class)
                    .hasMessage(null);

            // then
            assertThat(queryCaptor).extracting(ArgumentCaptor::getValue)
                    .extracting("firstName", "lastName", "gender", "birthDate", "birthPlace")
                    .containsExactly("mpu", "md", gender, birthDate, "anywhere");

            mockedStatic.verify(() -> Person.register(command), never());
            verify(personPersistanceRepository, never()).push(person);
        }
    }
}
