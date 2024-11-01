package com.mpumd.poc.person.sb.jpa;

import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import com.mpumd.poc.person.sb.jpa.mapper.PersonDomainJPAMapper;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;


@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PersonJpaAdapter.class})
class PersonJpaAdapterITest {

    // TODO extract the postgres image name outside of here, anywhere who is possible de change easier it
    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:17-alpine");

    /* DEV : activate the block to fix the port and easy use an SQL client
     * to finally inspect manually the updated schema
     */
//    static {
//        // Internal PostgreSQL port
//        postgresContainer.withExposedPorts(5432);
//        // Bind host port 5432 to container port 5432
//        postgresContainer.setPortBindings(List.of("5432:5432"));
//    }

    @Autowired
    PersonJpaAdapter adapter;
    @Autowired
    TestEntityManager entityManager;

    EasyRandom easyRandom = new EasyRandom();

    @AfterAll
    static void afterAll() {
        postgresContainer.close();
    }

    // TODO how to track the usage of the hibernate converter with spy it an verify ?
    @Test
    void pushInDBWithoutError() {
        Person aggregatRoot = new EasyRandom(new EasyRandomParameters().collectionSizeRange(5, 5))
                .nextObject(Person.class);
        assertThat(aggregatRoot).hasNoNullFieldsOrProperties();

        try (var mapper = mockStatic(PersonDomainJPAMapper.class, InvocationOnMock::callRealMethod)) {
            adapter.push(aggregatRoot);

            PersonEntity result = entityManager.find(PersonEntity.class, aggregatRoot.id());
            assertThat(result)
                    .usingRecursiveComparison()
                    .withEnumStringComparison()
                    .ignoringFields("genderChangeHistory")
                    .isEqualTo(aggregatRoot);

            // non control on order of elements of maps
            assertThat(result.genderChangeHistory())
                    .containsAllEntriesOf(aggregatRoot.genderChangeHistory());

            mapper.verify(() -> PersonDomainJPAMapper.toJpa(aggregatRoot));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "mpu, mpu, md, md, true",  // exact match ok
            "mpu, MPU, md, MD, true",  // ignore case ok
            "mpu, cge, md, MD, false",  // firstName is different
            "mpu, MPU, md, test, false",  // lastName is different
    })
    void isExistIsTrueIfPersonInsideDB(
            String queryFirstName, String dbFirstName,
            String queryLastName, String dbLastName,
            boolean exist) {
        // given
        var query = easyRandom.nextObject(PersonSearchQuery.class);
        ReflectionTestUtils.setField(query, "firstName", queryFirstName);
        ReflectionTestUtils.setField(query, "lastName", queryLastName);
        assertThat(query).hasNoNullFieldsOrProperties();

        PersonEntity entity = easyRandom.nextObject(PersonEntity.class);
        entity.id(UUID.randomUUID());
        entity.firstName(dbFirstName);
        entity.lastName(dbLastName);
        entity.birthPlace(query.birthPlace());
        entity.birthDate(query.birthDate());
        entity.genderChangeHistory(Map.of(query.birthDate().toLocalDateTime(), query.gender()));
        entityManager.persist(entity);

        try (var mapper = mockStatic(PersonDomainJPAMapper.class, InvocationOnMock::callRealMethod)) {
            // when then
            assertEquals(adapter.isExist(query), exist);
            mapper.verify(() -> PersonDomainJPAMapper.toJpa(query));
        }
    }

    @Test
    void isExistIsFalseIfDifferentPerson() {
        var query = easyRandom.nextObject(PersonSearchQuery.class);
        assertThat(query).hasNoNullFieldsOrProperties();

        // 2 persons in db
        IntStream.range(0, 2).mapToObj(i -> easyRandom.nextObject(PersonEntity.class))
                .peek(personEntity -> assertThat(personEntity).hasNoNullFieldsOrProperties())
                .forEach(entityManager::persist);

        try (var mapper = mockStatic(PersonDomainJPAMapper.class, InvocationOnMock::callRealMethod)) {
            assertFalse(adapter.isExist(query));
            mapper.verify(() -> PersonDomainJPAMapper.toJpa(query));
        }
    }
}