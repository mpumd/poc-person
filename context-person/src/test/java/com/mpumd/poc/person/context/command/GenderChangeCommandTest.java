package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.Gender;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GenderChangeCommandTest {

    @Test
    void OK() {
        val cmd = new GenderChangeCommand(Gender.FEMALE, LocalDateTime.now());
        assertThat(cmd).hasNoNullFieldsOrProperties();
    }

    @Test
    void KO_gender() {
        assertThatThrownBy(() -> new GenderChangeCommand(null, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("gender must not be null");
    }

    @Test
    void KO_changeDate() {
        assertThatThrownBy(() -> new GenderChangeCommand(Gender.FEMALE, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("changeDate must not be null");
    }
}
