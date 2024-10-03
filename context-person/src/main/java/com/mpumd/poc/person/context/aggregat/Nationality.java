package com.mpumd.poc.person.context.aggregat;

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
}
