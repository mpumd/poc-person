package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.utils.builder.PhysicalAppearanceBuilder;
import com.mpumd.poc.person.context.utils.builder.PhysicalAppearanceBuilders;
import org.jilt.Builder;
import org.jilt.BuilderStyle;

import static java.util.Optional.ofNullable;

public class PhysicalAppearance {
    private final short size; // in centimeters
    private final short weight; // kilogramme
    private final EyesColor eyesColor;
    private final HairColor hairColor;
    // private final byte[] face;

    // EMPTY value use like @MonotonicNonNull from checkerframework
    static final PhysicalAppearance EMPTY = new PhysicalAppearance(
            (short) 1,
            (short) 1,
            EyesColor.BLACK,
            HairColor.BLACK);

    // creation entry
    @Builder(style = BuilderStyle.STAGED, packageName = "com.mpumd.poc.person.context.utils.builder")
    public PhysicalAppearance(short size, short weight, EyesColor eyesColor, HairColor hairColor) {
        this.size = isPositiveOrThrow(size, "size");
        this.weight = isPositiveOrThrow(weight, "weight");
        this.eyesColor = isPresentOrThrow(eyesColor, "eyesColor");
        this.hairColor = isPresentOrThrow(hairColor, "hairColor");
    }

    // factory method introduce the first builder method
    public static PhysicalAppearanceBuilders.Size inform() {
        return PhysicalAppearanceBuilder.physicalAppearance();
    }

    private static <T> T isPresentOrThrow(T val, String fieldName) {
        return ofNullable(val).orElseThrow(() -> new IllegalArgumentException("%s can't be empty".formatted(fieldName)));
    }

    private static short isPositiveOrThrow(short val, String fieldName) {
        if (val <= 0) {
            throw new IllegalArgumentException("value was %s but %s is always positive".formatted(val, fieldName));
        }
        return val;
    }
}
