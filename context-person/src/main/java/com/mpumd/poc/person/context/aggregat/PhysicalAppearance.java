package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.command.InformPhysicalAppearanceCommand;
import lombok.NonNull;

import static java.util.Optional.ofNullable;

public class PhysicalAppearance {
    private final short size; // in centimeters
    private final short weight; // kilogramme
    private final EyesColor eyesColor;
    private final HairColor hairColor;

    // private final byte[] face;

    private PhysicalAppearance(@NonNull InformPhysicalAppearanceCommand cmd) {
        this.size = ofNullable(cmd.size())
                .filter(size -> size > 0)
                .orElseThrow(() -> new IllegalArgumentException("value was " + cmd.size() + " but size is always positive"));
        this.weight = ofNullable(cmd.weight())
                .filter(weight -> weight > 0)
                .orElseThrow(() -> new IllegalArgumentException("value was " + cmd.weight() + " but weight is always positive"));
        this.eyesColor = ofNullable(cmd.eyesColor())
                .orElseThrow(() -> new IllegalArgumentException("eyesColor can't be empty"));
        this.hairColor = ofNullable(cmd.hairColor())
                .orElseThrow(() ->  new IllegalArgumentException("hairColor can't be empty"));
    }

    public static PhysicalAppearance inform(InformPhysicalAppearanceCommand cmd) {
        return new PhysicalAppearance(cmd);
    }
}
