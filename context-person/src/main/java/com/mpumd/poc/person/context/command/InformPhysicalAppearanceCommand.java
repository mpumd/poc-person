package com.mpumd.poc.person.context.command;

import com.mpumd.poc.person.context.aggregat.EyesColor;
import com.mpumd.poc.person.context.aggregat.HairColor;
import com.mpumd.poc.person.context.command.builder.InformPhysicalAppearanceCommandBuilder;
import com.mpumd.poc.person.context.command.builder.InformPhysicalAppearanceCommandBuilders;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.jilt.Builder;
import org.jilt.BuilderStyle;


// TODO to record
@Getter
@Accessors(fluent = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InformPhysicalAppearanceCommand {
    short size;
    short weight;
    EyesColor eyesColor;
    HairColor hairColor;

    @Builder(style = BuilderStyle.STAGED, packageName = "com.mpumd.poc.person.context.command.builder")
    public InformPhysicalAppearanceCommand(short size, short weight, EyesColor eyesColor, HairColor hairColor) {
        this.size = size;
        this.weight = weight;
        this.eyesColor = eyesColor;
        this.hairColor = hairColor;
    }

    public static InformPhysicalAppearanceCommandBuilders.Size builder() {
        return InformPhysicalAppearanceCommandBuilder.informPhysicalAppearanceCommand();
    }
}
