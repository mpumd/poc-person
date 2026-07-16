package com.mpumd.poc.person.context.utils;

import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

public final class ObjectsUtils {
    private ObjectsUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T, E extends RuntimeException> T notNull(T val, Supplier<E> exceptionSupplier) {
        return ofNullable(val).orElseThrow(exceptionSupplier);
    }

    public static <T> T notNull(T val, String msg) {
        return notNull(val, () -> new IllegalArgumentException(msg));
    }

    public static <E extends RuntimeException> String notBlank(String val, Supplier<E> exceptionSupplier) {
        return ofNullable(val).filter(s -> !s.isBlank()).orElseThrow(exceptionSupplier);
    }

    public static String notBlank(String val, String msg) {
        return notBlank(val, () -> new IllegalArgumentException(msg));
    }
}
