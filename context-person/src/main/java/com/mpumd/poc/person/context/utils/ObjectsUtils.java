package com.mpumd.poc.person.context.utils;

import static java.util.Optional.ofNullable;

public final class ObjectsUtils {

    private ObjectsUtils() {
    }

    public static String notBlank(String val, String msg) {
        return ofNullable(val)
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new IllegalArgumentException(msg));
    }
}
