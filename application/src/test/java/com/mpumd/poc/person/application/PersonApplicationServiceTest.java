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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    PersonApplicationService personApplicationService;

    PersonRegistrationCommand command;

    @BeforeEach
    void setUp() {
        personApplicationService = new PersonApplicationService(personPersistanceRepository) {
        };
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
    void throwIllegalArgExAtNullDependencyInjection() {
        assertThatThrownBy(() -> new PersonApplicationService(null) {
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("personPersistanceRepository is marked non-null but is null");
    }

    @Test
    void registerNewPerson() {
        var queryCaptorFromRepo = ArgumentCaptor.forClass(PersonSearchQuery.class);
        given(personPersistanceRepository.isExist(queryCaptorFromRepo.capture())).willReturn(false);

        UUID id = UUID.randomUUID();
        given(person.id()).willReturn(id);

        try (var personMockedStatic = mockStatic(Person.class);
             var queryConstructorMock = mockConstruction(PersonSearchQuery.class)) {

            personMockedStatic.when(() -> Person.register(command)).thenReturn(person);

            // when
            UUID result = personApplicationService.register(command);

            // then
            assertEquals(
                    queryConstructorMock.constructed().get(0),
                    queryCaptorFromRepo.getValue()
            );
            assertEquals(result, id);
            verify(personPersistanceRepository).push(person);
        }
    }

    @Test
    void registerThrowAlreadyExistEx() {
        var queryPassedToRepo = ArgumentCaptor.forClass(PersonSearchQuery.class);
        given(personPersistanceRepository.isExist(queryPassedToRepo.capture())).willReturn(true);

        try (var mockedStatic = mockStatic(Person.class);
             var queryConstructorMock = mockConstruction(PersonSearchQuery.class, (queryMock, context) -> {
                 when(queryMock.firstName()).thenReturn("mpu");
                 when(queryMock.lastName()).thenReturn("md");
             })
        ) {
            // when
            assertThatThrownBy(() -> personApplicationService.register(command))
                    .isInstanceOf(PersonAlreadyExistException.class)
                    .hasMessage("mpu md already exist !");

            // then
            assertThat(queryConstructorMock.constructed())
                    .hasSize(1)
                    .first()
                    .isEqualTo(queryPassedToRepo.getValue());


            mockedStatic.verify(() -> Person.register(command));
            verify(personPersistanceRepository, never()).push(person);
        }
    }
}
