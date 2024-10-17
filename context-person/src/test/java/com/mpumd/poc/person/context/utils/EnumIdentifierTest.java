package com.mpumd.poc.person.context.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EnumIdentifierTest {

    @ParameterizedTest
    @ValueSource(strings = {"MICROSECONDS", "MicroSeCOnDS"})
    void buildByInsensitiveName(String name) {
        assertThat(EnumIdentifier.valueOfInsensitiveName(TimeUnit.class, name))
                .isEqualTo(TimeUnit.MICROSECONDS);
    }

    @Test
    void throwIllegalArgExOnWrongName() {
        assertThatThrownBy(() -> EnumIdentifier.valueOfInsensitiveName(TimeUnit.class, "femtoseconds"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("unknown name femtoseconds for enum class java.util.concurrent.TimeUnit");
    }

}