package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.utils.EnumIdentifier;

public enum Gender {
    MALE,
    FEMALE,
    HERMAPHRODITE,
    ALIEN,
    ;

    public static Gender valueOfName(String name) {
        return EnumIdentifier.valueOfInsensitiveName(Gender.class, name);
    }
}
