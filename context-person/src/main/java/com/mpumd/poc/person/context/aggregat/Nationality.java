package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.utils.EnumIdentifier;
import lombok.NonNull;

import java.util.Arrays;

public enum Nationality {
    FR("francaise"),
    EN("english"),
    TT("titan"),
    US("american"),
    CA("canadian"),
    ;

    private final String label;

    Nationality(String label) {
        this.label = label;
    }

    public static Nationality valueOfName(@NonNull String name) {
        if (name.length() > 2) {
            return Arrays.stream(Nationality.values())
                    .filter(lbl -> lbl.label.equals(name.toLowerCase()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("unknown name or label %s for enum class %s".formatted(name, Nationality.class.getSimpleName())));
        }
        return EnumIdentifier.valueOfInsensitiveName(Nationality.class, name);
    }
}
