package com.mpumd.poc.person.context.utils;

import lombok.NonNull;

public final class EnumIdentifier {

    private EnumIdentifier() {
    }

    // TODO we can use the string names + equalsIgnoreCase instead of uppercase.
    public static <T extends Enum<T>> T valueOfInsensitiveName(@NonNull Class<T> enumClass, @NonNull String name) {
        try {
            return Enum.valueOf(enumClass, name.toUpperCase());
        } catch (IllegalArgumentException _) {
            throw new IllegalArgumentException("unknown name %s for enum class %s".formatted(name, enumClass.getName()));
        }
    }

}
