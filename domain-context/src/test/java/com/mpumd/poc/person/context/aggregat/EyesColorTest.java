package com.mpumd.poc.person.context.aggregat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EyesColorTest {

    /** track new value if the dev forget to write the test, so test after */
    @Test
    void shouldHaveRightNumberOfEnumeration() {
        assertThat(EyesColor.values()).hasSize(6);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BROWN", "BLUE", "GREEN", "BLACK", "GREY", "HAZELNUT"})
    void shouldValidEnum(EyesColor enumerate) {
        assertNotNull(enumerate);
    }

}