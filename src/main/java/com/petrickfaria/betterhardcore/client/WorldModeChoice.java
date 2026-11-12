package com.petrickfaria.betterhardcore.client;

/** The mutually exclusive choices displayed by the vanilla world-creation game-mode control. */
public enum WorldModeChoice {
    SURVIVAL("SURVIVAL", "selectWorld.gameMode.survival"),
    CREATIVE("CREATIVE", "selectWorld.gameMode.creative"),
    HARDCORE("HARDCORE", "selectWorld.gameMode.hardcore"),
    SOFTCORE("SURVIVAL", "betterhardcore.mode.softcore");

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

    public static WorldModeChoice fromVanillaName(String name) {
        if ("CREATIVE".equals(name)) return CREATIVE;
        if ("HARDCORE".equals(name)) return HARDCORE;
        return SURVIVAL;
    }
}
