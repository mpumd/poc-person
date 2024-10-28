package com.mpumd.poc.person.context.aggregat;

import com.mpumd.poc.person.context.utils.EnumIdentifier;

public enum HairColor {
    BLOND,
    CHESTNUT,
    BROWN,
    BLACK,
    RED,
    GREY,
    WHITE,
    ;

    public static HairColor valueOfName(String name) {
        return EnumIdentifier.valueOfInsensitiveName(HairColor.class, name);
    }

}
