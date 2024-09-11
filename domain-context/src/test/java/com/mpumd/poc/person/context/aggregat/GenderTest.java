package com.mpumd.poc.person.context.aggregat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class GenderTest {

    /** track test after */
    @Test
    void shouldHave4Enumerate() {
        assertThat(Gender.values()).hasSize(4);
    }

    @ParameterizedTest
    @ValueSource(strings = { "MALE", "FEMALE", "HERMAPHRODITE", "ALIEN"})
    void shouldValidGender(Gender g) {
        // junit convert is the test. no need use assertThat
    }
}