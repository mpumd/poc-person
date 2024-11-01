package com.mpumd.poc.person.application.exception;

import org.junit.jupiter.api.Test;

import static lombok.Lombok.sneakyThrow;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonNotFoundExceptionTest {

    @Test
    void throwExDefaultConstructor() {
        assertThatThrownBy(() -> sneakyThrow(new PersonNotFoundException()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasNoCause()
                .hasMessage(null);
    }


    @Test
    void throwExWithMessage() {
        assertThatThrownBy(() -> sneakyThrow(new PersonNotFoundException("Person not exist!")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Person not exist!")
                .hasNoCause();
    }

    @Test
    void throwExWithCause() {
        Throwable cause = new IllegalArgumentException("Underlying cause");

        assertThatThrownBy(() -> sneakyThrow(new PersonNotFoundException(cause)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Underlying cause")
                .cause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Underlying cause");
    }

    @Test
    void throwExWithMessageAndCause() {
        String errorMessage = "Person not exist!";
        Throwable cause = new IllegalCallerException("Underlying cause");

        assertThatThrownBy(() -> sneakyThrow(new PersonNotFoundException(errorMessage, cause)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Person not exist!")
                .cause()
                .isInstanceOf(IllegalCallerException.class)
                .hasMessage("Underlying cause");
    }

}