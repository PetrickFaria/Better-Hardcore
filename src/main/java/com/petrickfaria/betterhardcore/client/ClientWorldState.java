package com.petrickfaria.betterhardcore.client;

public final class ClientWorldState {
    private static volatile boolean softcore;
    private ClientWorldState() {}
    public static boolean isSoftcore() { return softcore; }
    public static void setSoftcore(boolean value) { softcore = value; }
}
