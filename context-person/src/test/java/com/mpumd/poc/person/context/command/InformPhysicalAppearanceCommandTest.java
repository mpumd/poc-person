package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.EyesColor;
import com.mpumd.poc.person.context.aggregat.HairColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InformPhysicalAppearanceCommandTest {

    @Test
    void buildOK() {
        var cmd = InformPhysicalAppearanceCommand.builder()
                .size((short) 175)
                .weight((short) 75)
                .eyesColor(EyesColor.GREEN)
                .hairColor(HairColor.BLACK)
                .build();


        assertEquals(cmd.size(), 175);
        assertEquals(cmd.weight(), 75);
        assertEquals(cmd.eyesColor(), EyesColor.GREEN);
        assertEquals(cmd.hairColor(), HairColor.BLACK);
    }
}