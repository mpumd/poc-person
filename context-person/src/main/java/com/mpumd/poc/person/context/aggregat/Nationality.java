package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.utils.EnumIdentifier;

public enum Nationality {
    FR("francaise"),
    EN("english"),
    TT("titan"),
    US("american"),
    ;

    private final String label;

    Nationality(String label) {
        this.label = label;
    }

    public static Nationality valueOfName(String name) {
        return EnumIdentifier.valueOfInsensitiveName(Nationality.class, name);
    }
}
