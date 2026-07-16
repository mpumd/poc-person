package com.mpumd.poc.person.context.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonRehydrationExceptionTest {

    @Test
    void holdTheMessageAndNoCause() {
        assertThat(new PersonRehydrationException("person 42: corrupted"))
                .hasMessage("person 42: corrupted")
                .hasNoCause();
    }

    @Test
    void formatTheMessageWithTheGivenArgs() {
        assertThat(new PersonRehydrationException("person %s: %s must not be blank", 42, "firstName"))
                .hasMessage("person 42: firstName must not be blank");
    }

    @Test
    void isAnIllegalStateException() {
        assertThat(new PersonRehydrationException("boom"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");
    }
}
