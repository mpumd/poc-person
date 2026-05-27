package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.EyesColor;
import com.mpumd.poc.person.context.aggregat.HairColor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


class InformPhysicalAppearanceCommandTest {

    InformPhysicalAppearanceCommandBuilder builder;

//    @BeforeEach
//    void setUp() {
//        builder = Instancio.create(InformPhysicalAppearanceCommandBuilder.class);
//        ReflectionTestUtils.setField(builder, "size", (short) 175);
//        ReflectionTestUtils.setField(builder, "weight", (short) 75);
//        assertThat(builder).hasNoNullFieldsOrProperties();
//    }

    @Test
    void OK() {
        var cmd = InformPhysicalAppearanceCommand.builder()
                .size((short) 175)
                .weight((short) 75)
                .eyesColor(EyesColor.GREEN)
                .hairColor(HairColor.BLACK)
                .build();

        assertEquals(175, cmd.size());
        assertEquals(75, cmd.weight());
        assertEquals(EyesColor.GREEN, cmd.eyesColor());
        assertEquals(HairColor.BLACK, cmd.hairColor());
    }

    @Test
    void OK_empty() {
        var cmd = InformPhysicalAppearanceCommand.builder()
                .size((short) 0)
                .weight((short) 0)
                .eyesColor(null)
                .hairColor(null)
                .build();

        assertThat(cmd)
                .isNotNull();
        assertEquals(0, cmd.size());
        assertEquals(0, cmd.weight());
        assertThat(cmd.eyesColor()).isNull();
        assertThat(cmd.hairColor()).isNull();
    }


}