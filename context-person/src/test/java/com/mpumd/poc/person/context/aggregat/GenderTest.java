package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.utils.EnumIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GenderTest {

    /**
     * track test after
     */
    @Test
    void have4Enumerate() {
        assertThat(Gender.values()).hasSize(4);
    }

    @ParameterizedTest
    @ValueSource(strings = {"MALE", "FEMALE", "HERMAPHRODITE", "ALIEN"})
    void validGender(Gender g) {
        assertNotNull(g);
    }

    @Test
    void buildByInsensitiveName() {
        try (var enumIdSM = Mockito.mockStatic(EnumIdentifier.class)) {
            enumIdSM.when(() -> EnumIdentifier.valueOfInsensitiveName(Gender.class, "male"))
                    .thenReturn(Gender.MALE);
            assertThat(Gender.valueOfName("male"))
                    .isEqualTo(Gender.MALE);
        }
    }
}