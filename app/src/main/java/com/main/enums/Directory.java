package com.main.enums;

public enum Directory {
    PANTRY("pantry"),
    INGREDIENTS("ingredients");
    private final String value;

    Directory(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
