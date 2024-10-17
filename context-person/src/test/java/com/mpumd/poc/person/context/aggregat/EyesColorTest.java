package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.utils.EnumIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EyesColorTest {

    /**
     * track new value if the dev forget to write the test, so test after
     */
    @Test
    void shouldHaveRightNumberOfEnumeration() {
        assertThat(EyesColor.values()).hasSize(6);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BROWN", "BLUE", "GREEN", "BLACK", "GREY", "HAZELNUT"})
    void shouldValidEnum(EyesColor enumerate) {
        // if the enumerate name change, the valueof method will throw an IllegalArgEx.
        // this following assert isn't even unnecessary :)
        assertNotNull(enumerate);
    }

    @Test
    void buildByInsensitiveName() {
        try (var enumIdSM = Mockito.mockStatic(EnumIdentifier.class)) {
            enumIdSM.when(() -> EnumIdentifier.valueOfInsensitiveName(EyesColor.class, "blue"))
                    .thenReturn(EyesColor.BLUE);
            assertThat(EyesColor.valueOfName("blue"))
                    .isEqualTo(EyesColor.BLUE);
        }
    }

}