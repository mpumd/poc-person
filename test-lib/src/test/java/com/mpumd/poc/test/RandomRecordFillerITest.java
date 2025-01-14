package com.mpumd.poc.test;

import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.reflect.AccessFlag.Location;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class RandomRecordFillerITest {

    @Test
    void fillAllRecordWithRandomValues() {
        var recordInstance = RandomRecordFiller.fillRandomly(StandardRecord.class);

        try (var randomRecordFillerSpy = mockStatic(RandomRecordFiller.class, InvocationOnMock::callRealMethod)) {
            assertThat(recordInstance)
                    .hasNoNullFieldsOrProperties()
                    .satisfies(record -> assertThat(record.string()).isNotBlank())
                    // random values so different in the second instance
                    .isNotEqualTo(RandomRecordFiller.fillRandomly(StandardRecord.class));

            randomRecordFillerSpy.verify(() -> RandomRecordFiller.generateRandomValue(any(Class.class)), times(StandardRecord.class.getRecordComponents().length));
        }
    }

    @Test
    void fillRecordWithRandomValuesExceptForcedFields() {
        // given
        int fInt = 1234;
        long fLong = 3456;
        Double fDouble = 834792.64;
        UUID uuid = UUID.randomUUID();
        Boolean fBoolean = Boolean.TRUE;
        String string = "bonjorno";
        Location enumLocation = Location.MODULE_REQUIRES;
        //
        LocalDate localDate = LocalDate.now();
        LocalDateTime localDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        Instant instant = Instant.now();

        Map<String, Object> fieldReplacement = new HashMap<>();
        fieldReplacement.putAll(Map.of(
                "integer", fInt,
                "fLong", fLong,
                "fDouble", fDouble,
                "uuid", uuid,
                "fBoolean", fBoolean,
                "string", string,
                "enumLocation", enumLocation));
        fieldReplacement.putAll(Map.of(
                "localDate", localDate,
                "localDateTime", localDateTime,
                "zonedDateTime", zonedDateTime,
                "instant", instant
        ));

        try (var randomRecordFillerSpy = mockStatic(RandomRecordFiller.class, InvocationOnMock::callRealMethod)) {
            // when
            var recordInstance = RandomRecordFiller.fillRandomly(StandardRecord.class, fieldReplacement);

            // then
            assertThat(recordInstance)
                    .extracting(
                            "integer", "fLong", "fDouble", "uuid", "fBoolean", "string", "enumLocation",
                            "localDate", "localDateTime", "zonedDateTime", "instant")
                    .containsExactly(fInt, fLong, fDouble, uuid, fBoolean, string, enumLocation,
                            localDate, localDateTime, zonedDateTime, instant);

            randomRecordFillerSpy.verify(() -> RandomRecordFiller.generateRandomValue(any(Class.class)), never());

            // no random values. All of them are forced by the map.
            assertThat(recordInstance)
                    .isEqualTo(RandomRecordFiller.fillRandomly(StandardRecord.class, fieldReplacement));
        }
    }

    @Test
    void throwUnsupportedEx() {
        assertThatThrownBy(() -> RandomRecordFiller.fillRandomly(UnsupportedTypeRecord.class))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Type not supported : class java.util.ArrayList");
    }
}