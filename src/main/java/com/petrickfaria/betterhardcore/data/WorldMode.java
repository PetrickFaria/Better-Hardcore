package com.petrickfaria.betterhardcore.data;

public enum WorldMode {
    VANILLA, HARDCORE, SOFTCORE;

    public static WorldMode parse(String value) {
        try { return valueOf(value); } catch (RuntimeException ignored) { return VANILLA; }
    }
}
