package com.petrickfaria.betterhardcore.client;

/** One-shot selection consumed when the integrated server for a newly created world starts. */
public final class SoftcoreSelection {
    private static boolean selected;
    private SoftcoreSelection() {}
    public static boolean isSelected() { return selected; }
    public static void setSelected(boolean value) { selected = value; }
    public static boolean consume() { boolean value = selected; selected = false; return value; }
}
