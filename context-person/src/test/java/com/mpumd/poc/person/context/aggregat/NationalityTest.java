package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.utils.EnumIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class NationalityTest {

    @Test
    void TDD_approch_please() {
        assertThat(Arrays.stream(Nationality.values()).map(Enum::name).toArray(String[]::new))
                .hasSize(4)
                .containsExactly("FR", "EN", "TT", "US");
    }

    @ParameterizedTest
    @CsvSource({
            "FR, francaise",
            "EN, english",
            "TT, titan",
            "US, american",
    })
    void shouldHaveExactlyLikeFollowing(Nationality enumType, String label) {
        // no need to test the name of enum, the junit converter will fail on a wrong name
        assertThat(enumType).extracting("label").isEqualTo(label);
    }

    @Test
    void buildByInsensitiveName() {
        try (var enumIdSM = mockStatic(EnumIdentifier.class)) {
            enumIdSM.when(() -> EnumIdentifier.valueOfInsensitiveName(Nationality.class, "fr"))
                    .thenReturn(Nationality.FR);
            assertThat(Nationality.valueOfName("fr"))
                    .isEqualTo(Nationality.FR);
        }
    }
}