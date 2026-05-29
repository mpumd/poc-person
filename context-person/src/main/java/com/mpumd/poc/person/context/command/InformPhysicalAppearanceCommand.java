package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.EyesColor;
import com.mpumd.poc.person.context.aggregat.HairColor;

import com.mpumd.poc.person.context.builder.InformPhysicalAppearanceCommandBuilder;
import org.jilt.Builder;
import org.jilt.BuilderStyle;

@Builder(style = BuilderStyle.STAGED, packageName = "com.mpumd.poc.person.context.builder")
public record InformPhysicalAppearanceCommand(
        short size,
        short weight,
        EyesColor eyesColor,
        HairColor hairColor) {

    public static com.mpumd.poc.person.context.builder.InformPhysicalAppearanceCommandBuilders.Size builder() {
        return InformPhysicalAppearanceCommandBuilder.informPhysicalAppearanceCommand();
    }
}
