package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.command.InformPhysicalAppearanceCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhysicalAppearanceTest {

    InformPhysicalAppearanceCommand cmd = InformPhysicalAppearanceCommand.builder()
            .size((short) 175)
            .weight((short) 75)
            .eyesColor(EyesColor.BLACK)
            .hairColor(HairColor.GREY)
            .build();

    @Test
    void OK() {
        assertThat(PhysicalAppearance.inform(cmd))
                .extracting("size", "weight", "eyesColor", "hairColor")
                .containsExactly((short) 175, (short) 75, EyesColor.BLACK, HairColor.GREY);
    }

    @Test
    void KO_CommandNull() {
        assertThatThrownBy(() -> PhysicalAppearance.inform(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("cmd is marked non-null but is null");
    }

    @ParameterizedTest
    @ValueSource(shorts = {-7, 0})
    void KO_size(short val) {
        ReflectionTestUtils.setField(cmd, "size", val);
        assertThatThrownBy(() -> PhysicalAppearance.inform(cmd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value was %s but size is always positive", val);
    }

    @ParameterizedTest
    @ValueSource(shorts = {-7, 0})
    void KO_weight(short val) {
        ReflectionTestUtils.setField(cmd, "weight", val);
        assertThatThrownBy(() -> PhysicalAppearance.inform(cmd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("value was %s but weight is always positive", val);
    }

    @Test
    void KO_eyesColor() {
        ReflectionTestUtils.setField(cmd, "eyesColor", null);
        assertThatThrownBy(() -> PhysicalAppearance.inform(cmd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("eyesColor can't be empty");
    }

    @Test
    void KO_hairColor() {
        ReflectionTestUtils.setField(cmd, "hairColor", null);
        assertThatThrownBy(() -> PhysicalAppearance.inform(cmd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("hairColor can't be empty");
    }
}