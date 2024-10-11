package com.mpumd.poc.person.sb.jpa;

import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import com.mpumd.poc.person.sb.jpa.mapper.DomainJPAMapper;
import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

@SpringBootApplication // require by @DataJpaTest when we haven't any config class or SBAppRunner
class Runner {
}

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PersonJpaAdapter.class)
class PersonJpaAdapterIT {

    // TODO extract the postgres image name outside of here, anywhere who is possible de change easier it
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgre = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    PersonJpaAdapter adapter;
    @Autowired
    TestEntityManager entityManager;

    EasyRandom easyRandom = new EasyRandom();

    @Test
    void pushInDBWithoutError() {
        Person aggregatRoot = easyRandom.nextObject(Person.class);
        assertThat(aggregatRoot).hasNoNullFieldsOrProperties();

        try (var mapper = mockStatic(DomainJPAMapper.class, InvocationOnMock::callRealMethod)) {
            adapter.push(aggregatRoot);

            assertThat(entityManager.find(PersonEntity.class, aggregatRoot.id()))
                    .usingRecursiveComparison()
                    .withEnumStringComparison()
                    .isEqualTo(aggregatRoot);

            mapper.verify(() -> DomainJPAMapper.toJpa(aggregatRoot));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "mpu, mpu, md, md",  // exact match
            "mpu, MPU, md, MD",  // ignore case
    })
    void isExistIsTrueIfPersonInsideDB(String queryFirstName, String dbFirstName, String queryLastName, String dbLastName) {
        var query = easyRandom.nextObject(PersonSearchQuery.class);

        replaceInside(query, "firstName", queryFirstName);
        replaceInside(query, "lastName", queryLastName);

        assertThat(query).hasNoNullFieldsOrProperties();

        PersonEntity entity = easyRandom.nextObject(PersonEntity.class);
        entity.id(UUID.randomUUID());
        entity.firstName(dbFirstName);
        entity.lastName(dbLastName);
        entity.birthPlace(query.birthPlace());
        entity.birthDate(query.birthDate());
        entity.gender(query.gender().toString());

        entityManager.persist(entity);

        try (var mapper = mockStatic(DomainJPAMapper.class, InvocationOnMock::callRealMethod)) {

            assertTrue(adapter.isExist(query));
            mapper.verify(() -> DomainJPAMapper.toJpa(query));
        }
    }

    @SneakyThrows
    private static <T> void replaceInside(T instance, String fieldName, Object value) {
        var lnF = instance.getClass().getDeclaredField(fieldName);
        lnF.setAccessible(true);
        lnF.set(instance, value);
    }
}