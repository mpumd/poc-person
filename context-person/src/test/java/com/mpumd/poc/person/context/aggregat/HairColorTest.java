package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.utils.EnumIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

class HairColorTest {

    @Test
    void have7Enumerate() {
        assertThat(HairColor.values()).hasSize(7);
    }

    @ParameterizedTest
    @ValueSource(strings = { "BLOND", "CHESTNUT", "BROWN", "BLACK", "RED", "GREY", "WHITE"})
    void validHairColor(HairColor g) { assertNotNull(g); }

    @Test
    void buildByInsensitiveName() {
        try (var enumIdSM = mockStatic(EnumIdentifier.class)) {
            enumIdSM.when(() -> EnumIdentifier.valueOfInsensitiveName(HairColor.class, "blond"))
                    .thenReturn(HairColor.BLOND);
            assertThat(HairColor.valueOfName("blond"))
                    .isEqualTo(HairColor.BLOND);
        }
    }

}