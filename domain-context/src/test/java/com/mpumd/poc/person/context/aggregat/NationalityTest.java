package com.mpumd.poc.person.context.aggregat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class NationalityTest {

    @Test
    void TDD_approch_please() {
        var nationalities = Arrays.stream(Nationality.values()).map(Enum::name).toArray(String[]::new);
        assertThat(nationalities)
                .hasSize(3)
                .containsExactly("FR", "EN", "TT");
    }

    @Test
    void shouldOKFR() {
        assertThat(Nationality.FR).extracting(Enum::name).isEqualTo("FR");
        assertThat(Nationality.FR).extracting("label").isEqualTo("francaise");
    }

    @Test
    void shouldOKEN() {
        assertThat(Nationality.EN).extracting(Enum::name).isEqualTo("EN");
        assertThat(Nationality.EN).extracting("label").isEqualTo("english");
    }

    @Test
    void OK_titan() {
        assertThat(Nationality.TT).extracting(Enum::name).isEqualTo("TT");
        assertThat(Nationality.TT).extracting("label").isEqualTo("titan");
    }

    // NOTE the parametrized test seems to be a little bit complicate, but it was written after the previous tests
    // this is only a refactoring just for example.

    @ParameterizedTest
    @CsvSource({
            "FR, francaise",
            "EN, english"
    })
    void shouldHaveAnExactSyntax(Nationality enumType, String label) {
        // no need to test the name of enum, the junit converter will failed for a wrong name
        assertThat(enumType).extracting("label").isEqualTo(label);
    }
}