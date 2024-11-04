package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.utils.EnumIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

class NationalityTest {

    @Test
    void TDD_approch_please() {
        assertThat(Arrays.stream(Nationality.values()).map(Enum::name).toArray(String[]::new))
                .hasSize(5)
                .containsExactly("FR", "EN", "TT", "US", "CA");
    }

    @ParameterizedTest
    @CsvSource({
            "FR, francaise",
            "EN, english",
            "TT, titan",
            "US, american",
            "CA, canadian",
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

    @Test
    void buildByInsensitiveLabel() {
        try (var enumIdSM = mockStatic(EnumIdentifier.class)) {
            assertThat(Nationality.valueOfName("Canadian")).isEqualTo(Nationality.CA);
            enumIdSM.verify(() -> EnumIdentifier.valueOfInsensitiveName(Nationality.class, "ca"), never());
        }
    }

    @Test
    void throwIllegalArgExIfNullName() {
        assertThatThrownBy(() -> Nationality.valueOfName(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name is marked non-null but is null");
    }

    @Test
    void throwIllegalArgExIfUnknowName() {
        assertThatThrownBy(() -> Nationality.valueOfName("testLabelOrNameUnknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("unknown name or label testLabelOrNameUnknown for enum class Nationality");
    }
}