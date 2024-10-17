package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.utils.EnumIdentifier;

public enum EyesColor {
    BROWN, BLUE, GREEN, BLACK, GREY, HAZELNUT,
    ;

    public static EyesColor valueOfName(String name) {
        return EnumIdentifier.valueOfInsensitiveName(EyesColor.class, name);
    }
}
