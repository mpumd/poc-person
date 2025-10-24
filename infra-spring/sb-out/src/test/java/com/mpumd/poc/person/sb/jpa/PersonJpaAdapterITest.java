package com.mpumd.poc.person.sb.jpa;

import com.mpumd.poc.person.context.aggregat.Nationality;
import com.mpumd.poc.person.context.aggregat.Person;
import com.mpumd.poc.person.context.query.PersonSearchQuery;
import com.mpumd.poc.person.sb.jpa.entity.PersonEntity;
import com.mpumd.poc.person.sb.jpa.mapper.PersonDomainJPAMapper;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PersonJpaAdapter.class})
class PersonJpaAdapterITest {

    // TODO extract the postgres image name outside of here, anywhere who is possible de change easier it
    @Container
    @ServiceConnection
    private static final PostgreSQLContainer dbContainer = new PostgreSQLContainer("postgres:17-alpine");

    private static JdbcClient jdbcClient;

    static GeneratorSpecProvider<LocalDateTime> localDateTimeSpecTruncator = generators -> generators
            .temporal()
            .localDateTime()
            .truncatedTo(ChronoUnit.MICROS);

    /* DEV : activate the block to fix the port and easy use an SQL client
     * to finally inspect manually the updated schema
     */
    static {
        // Internal PostgreSQL port
        dbContainer.withExposedPorts(5432);
        // Bind host port 5432 to container port 5432
        dbContainer.setPortBindings(List.of("5432:5432"));
    }

    @Autowired
    PersonJpaAdapter adapter;
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    TransactionTemplate transactionTemplate;

    @SpyBean
    PersonSpringRepo personSpringRepo;

    @BeforeAll
    static void beforeAll() {
        jdbcClient = JdbcClient.create(new DriverManagerDataSource(
                dbContainer.getJdbcUrl(),
                dbContainer.getUsername(),
                dbContainer.getPassword()
        ));
    }

    @AfterAll
    static void afterAll() {
        dbContainer.close();
    }

    @BeforeEach
    void setUp() {
        jdbcClient.sql("DELETE FROM PERSON").update();
    }

    /* Why this method ?
     * Because, it's necessary to create a Component with SpringBoot to proxify a new transaction except if
     * we force manually a new transaction like bellow.
     * Otherwise we can't exclude the TestEntityManager.persist(e) form the current transaction initialize by @DataJpaTest
     * and finally you never test the writing of data in db and they stay in cache of hibernate during the test.
     * */
    private void forcePersistInDB(Runnable persistCaller) {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.execute(status -> {
            persistCaller.run();
            entityManager.flush(); // force to save in db
            entityManager.clear(); // clean cache
            return null;
        });

    }

    // TODO how to track the usage of the hibernate converter with spy it an verify ?
    @Test
    void pushInDBWithoutError() {
        Person aggregatRoot = Instancio.of(Person.class)
                .generate(all(LocalDateTime.class), localDateTimeSpecTruncator)
                .create();

        assertThat(aggregatRoot).hasNoNullFieldsOrProperties();

        try (var mapper = mockStatic(PersonDomainJPAMapper.class, InvocationOnMock::callRealMethod)) {
            // when
            forcePersistInDB(() -> adapter.push(aggregatRoot));

            // then
            assertThat(jdbcClient.sql("SELECT * FROM person").query().listOfRows())
                    .hasSize(1)
                    .first()
                    .asInstanceOf(InstanceOfAssertFactories.MAP)
                    .isNotEmpty();
            PersonEntity result = entityManager.find(PersonEntity.class, aggregatRoot.id());
            assertThat(result)
                    .usingRecursiveComparison()
                    .withEnumStringComparison()
                    .ignoringFields("genderChangeHistory")
                    .isEqualTo(aggregatRoot);

            assertThat(result.genderChangeHistory()).containsExactlyInAnyOrderEntriesOf(aggregatRoot.genderChangeHistory());
            mapper.verify(() -> PersonDomainJPAMapper.toJpa(aggregatRoot));
        }
    }

    @Test
    void pullFromDBWithoutError() {
        // GIVEN entity
        PersonEntity givenEntity = Instancio.of(PersonEntity.class)
                .generate(all(LocalDateTime.class), localDateTimeSpecTruncator)
                .create();

        ReflectionTestUtils.setField(givenEntity, "nationality", Nationality.FR.name());
        assertThat(givenEntity).hasNoNullFieldsOrProperties();
        forcePersistInDB(() -> entityManager.persist(givenEntity));

        try (var mapper = mockStatic(PersonDomainJPAMapper.class, InvocationOnMock::callRealMethod)) {
            // WHEN
            Person aggregateRoot = adapter.pull(givenEntity.id()).get();

            // THEN data exist in db no only inside the hibernate cache
            assertThat(jdbcClient.sql("SELECT * FROM person").query().listOfRows())
                    .hasSize(1)
                    .first()
                    .asInstanceOf(InstanceOfAssertFactories.MAP)
                    .isNotEmpty();
            // THEN aggregateRoot = givenJpaEntity
            assertThat(aggregateRoot)
                    .usingRecursiveComparison()
                    .withEnumStringComparison()
                    .ignoringFields("physicalAppearance")
                    .isEqualTo(givenEntity);

            verify(personSpringRepo).findById(eq(givenEntity.id()));

            // THEN loadEntity = givenEntity
            var loadedEntityCaptor = ArgumentCaptor.forClass(PersonEntity.class);
            mapper.verify(() -> PersonDomainJPAMapper.toDomain(loadedEntityCaptor.capture()));
            assertThat(loadedEntityCaptor.getValue())
                    .usingRecursiveComparison()
                    .isEqualTo(givenEntity);
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
        var query = Instancio.create(PersonSearchQuery.class);
        ReflectionTestUtils.setField(query, "firstName", queryFirstName);
        ReflectionTestUtils.setField(query, "lastName", queryLastName);
        assertThat(query).hasNoNullFieldsOrProperties();

        PersonEntity entity = Instancio.create(PersonEntity.class);
        entity.id(UUID.randomUUID());
        entity.firstName(dbFirstName);
        entity.lastName(dbLastName);
        entity.birthPlace(query.birthPlace());
        entity.birthDate(query.birthDate());
        entity.genderChangeHistory(Map.of(query.birthDate().toLocalDateTime(), query.gender()));
        forcePersistInDB(() -> entityManager.persist(entity));

        try (var mapper = mockStatic(PersonDomainJPAMapper.class, InvocationOnMock::callRealMethod)) {
            // when then
            assertThat(jdbcClient.sql("SELECT * FROM person").query().listOfRows()).hasSize(1);
            assertEquals(adapter.isExist(query), exist);
            mapper.verify(() -> PersonDomainJPAMapper.toJpa(query));
        }
    }

    @Test
    void isExistIsFalseIfDifferentPerson() {
        var query = Instancio.create(PersonSearchQuery.class);
        assertThat(query).hasNoNullFieldsOrProperties();

        // 2 persons in db
        IntStream.range(0, 2).mapToObj(i -> Instancio.create(PersonEntity.class))
                .peek(personEntity -> assertThat(personEntity).hasNoNullFieldsOrProperties())
                .forEach(e -> forcePersistInDB(() -> entityManager.persist(e)));

        assertThat(jdbcClient.sql("SELECT * FROM person").query().listOfRows()).hasSize(2);
        try (var mapper = mockStatic(PersonDomainJPAMapper.class, InvocationOnMock::callRealMethod)) {
            assertFalse(adapter.isExist(query));
            mapper.verify(() -> PersonDomainJPAMapper.toJpa(query));
        }
    }
}