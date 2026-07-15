package com.mpumd.poc.person.context.utils;

import static java.util.Optional.ofNullable;

public final class ObjectsUtils {
    private ObjectsUtils() {
        throw new UnsupportedOperationException();
    }

    public static String notBlank(String val, String msg) {
        return ofNullable(val)
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException(msg));
    }

    public static <T> T notNull(T val, String msg) {
        return ofNullable(val).orElseThrow(() -> new IllegalArgumentException(msg));
    }
}
