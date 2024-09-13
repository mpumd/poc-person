package com.mpumd.poc.person.context.aggregat;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhysicalAppearanceTest {

    @Test
    void OK() {
        short size = 175;
        short weight = 75;
        var eyesColor = EyesColor.BLACK;

        assertThat(PhysicalAppearance.inform(size, weight, eyesColor))
                .extracting("size", "weight", "eyesColor")
                .containsExactly(size, weight, eyesColor);
    }

    @Test
    void KO_size() {
        assertThatThrownBy(() -> PhysicalAppearance.inform((short) -3, (short) 75, EyesColor.BLACK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value was -3 but size is always positive");
    }

    @Test
    void KO_weight() {
        assertThatThrownBy(() -> PhysicalAppearance.inform((short) 175, (short) -5, EyesColor.BLACK))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value was -5 but weight is always positive");
    }

    @Test
    void KO_eyesColor() {
        assertThatThrownBy(() -> PhysicalAppearance.inform((short) 175, (short) 75, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("eyesColor can't be empty");
    }
}