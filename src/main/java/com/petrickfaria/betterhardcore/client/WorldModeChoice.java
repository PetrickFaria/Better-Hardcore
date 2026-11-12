package com.petrickfaria.betterhardcore.client;

import java.util.ArrayList;
import java.util.List;

/** The mutually exclusive choices displayed by the vanilla world-creation game-mode control. */
public enum WorldModeChoice {
    SURVIVAL("SURVIVAL", "selectWorld.gameMode.survival"),
    CREATIVE("CREATIVE", "selectWorld.gameMode.creative"),
    HARDCORE("HARDCORE", "selectWorld.gameMode.hardcore"),
    SOFTCORE("SURVIVAL", "betterhardcore.mode.softcore"),
    DEBUG("DEBUG", "selectWorld.gameMode.debug");

    private static final List<WorldModeChoice> STANDARD_CHOICES = List.of(
            SURVIVAL, CREATIVE, HARDCORE, SOFTCORE);

    private final String vanillaGameMode;
    private final String displayKey;

    WorldModeChoice(String vanillaGameMode, String displayKey) {
        this.vanillaGameMode = vanillaGameMode;
        this.displayKey = displayKey;
    }

    public String vanillaGameMode() {
        return vanillaGameMode;
    }

    public String displayKey() {
        return displayKey;
    }

    public boolean isSoftcore() {
        return this == SOFTCORE;
    }

    public static List<WorldModeChoice> standardChoices() {
        return STANDARD_CHOICES;
    }

    public static List<WorldModeChoice> choices(boolean includeDebug) {
        if (!includeDebug) return STANDARD_CHOICES;
        List<WorldModeChoice> choices = new ArrayList<>(STANDARD_CHOICES);
        choices.add(DEBUG);
        return List.copyOf(choices);
    }

    public static WorldModeChoice fromVanillaName(String name) {
        if ("CREATIVE".equals(name)) return CREATIVE;
        if ("HARDCORE".equals(name)) return HARDCORE;
        if ("DEBUG".equals(name)) return DEBUG;
        return SURVIVAL;
    }
}
