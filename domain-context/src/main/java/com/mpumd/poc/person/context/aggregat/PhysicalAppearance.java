package com.mpumd.poc.person.context.aggregat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PhysicalAppearance {
    private final short size; // in centimeters
    private final short weight; // kilogramme
    private final EyesColor eyesColor;

    public static PhysicalAppearance inform(short size, short weight, EyesColor eyesColor) {
        // REFACTOR use string template for size
        if (size < 0) throw new IllegalArgumentException("value was " + size + " but size is always positive");
        if (weight < 0) throw new IllegalArgumentException("value was " + weight + " but weight is always positive");
        if (eyesColor == null) throw new IllegalArgumentException("eyesColor can't be empty");
        return new PhysicalAppearance(size, weight, eyesColor);
    }
}
