package com.mpumd.poc.person.context.aggregat;

public enum Nationality {
    FR("francaise"),
    EN("english");

    private final String label;

    Nationality(String label) {
        this.label = label;
    }
}
