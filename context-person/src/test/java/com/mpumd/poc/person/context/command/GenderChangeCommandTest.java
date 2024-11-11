package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Gender;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenderChangeCommandTest {

    UUID id = UUID.randomUUID();
    Gender gender = Gender.FEMALE;
    LocalDateTime changeDate = LocalDateTime.now();

    @Test
    void throwExOnNullId() {
        assertThatThrownBy(() -> new GenderChangeCommand(null, gender, changeDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("id is marked non-null but is null");
    }

    @Test
    void throwExOnNullGender() {
        assertThatThrownBy(() -> new GenderChangeCommand(id, null, changeDate))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("gender is marked non-null but is null");
    }

    @Test
    void throwExOnNullChangeDate() {
        assertThatThrownBy(() -> new GenderChangeCommand(id, gender, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("changeDate is marked non-null but is null");
    }
}