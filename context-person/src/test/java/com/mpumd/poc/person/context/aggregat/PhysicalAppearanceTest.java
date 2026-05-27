package com.mpumd.poc.person.context.aggregat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhysicalAppearanceTest {
    PhysicalAppearanceBuilders.Optionals physicalAppearanceBuilder = PhysicalAppearance.inform()
            .size((short) 175)
            .weight((short) 75)
            .eyesColor(EyesColor.BLACK)
            .hairColor(HairColor.GREY);

    @Test
    void OK() {
        assertThat(physicalAppearanceBuilder.build())
                .extracting("size", "weight", "eyesColor", "hairColor")
                .containsExactly((short) 175, (short) 75, EyesColor.BLACK, HairColor.GREY);
    }

    @ParameterizedTest
    @ValueSource(shorts = {-7, 0})
    void KO_size(short val) {
        ReflectionTestUtils.setField(physicalAppearanceBuilder, "size", val);
        assertThatThrownBy(() -> physicalAppearanceBuilder.build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value was %s but size is always positive", val);
    }

    @ParameterizedTest
    @ValueSource(shorts = {-7, 0})
    void KO_weight(short val) {
        ReflectionTestUtils.setField(physicalAppearanceBuilder, "weight", val);
        assertThatThrownBy(() -> physicalAppearanceBuilder.build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value was %s but weight is always positive", val);
    }

    @Test
    void KO_eyesColor() {
        ReflectionTestUtils.setField(physicalAppearanceBuilder, "eyesColor", null);
        assertThatThrownBy(() -> physicalAppearanceBuilder.build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("eyesColor can't be empty");
    }

    @Test
    void KO_hairColor() {
        ReflectionTestUtils.setField(physicalAppearanceBuilder, "hairColor", null);
        assertThatThrownBy(() -> physicalAppearanceBuilder.build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("hairColor can't be empty");
    }
}