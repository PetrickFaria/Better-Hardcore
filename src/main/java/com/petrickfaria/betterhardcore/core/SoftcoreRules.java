package com.petrickfaria.betterhardcore.core;

/** Pure rules shared by the server implementation and automated tests. Values are health points, not hearts. */
public final class SoftcoreRules {
    public static final int START_HEALTH = 6;
    public static final int MAX_HEALTH = 30;
    public static final int DEATH_PENALTY = 2;
    public static final int MINIMUM_HEALTH = 2;
    public static final long TICKS_PER_REWARD = 30L * 60L * 20L;

    private SoftcoreRules() {}

    public record Progress(int maxHealth, long survivedTicks, boolean permanentlyDead) {}

    public static Progress tick(Progress current, boolean alive) {
        if (!alive || current.permanentlyDead()) return current;
        int health = Math.max(MINIMUM_HEALTH, Math.min(MAX_HEALTH, current.maxHealth()));
        long ticks = Math.max(0L, current.survivedTicks());
        if (health >= MAX_HEALTH) return new Progress(MAX_HEALTH, 0L, false);
        ticks++;
        while (ticks >= TICKS_PER_REWARD && health < MAX_HEALTH) {
            health = Math.min(MAX_HEALTH, health + 1);
            ticks -= TICKS_PER_REWARD;
        }
        if (health >= MAX_HEALTH) ticks = 0L;
        return new Progress(health, ticks, false);
    }

    public static Progress die(Progress current) {
        if (current.permanentlyDead()) return current;
        int health = Math.max(MINIMUM_HEALTH, Math.min(MAX_HEALTH, current.maxHealth()));
        if (health - DEATH_PENALTY < MINIMUM_HEALTH) {
            return new Progress(health, current.survivedTicks(), true);
        }
        return new Progress(health - DEATH_PENALTY, current.survivedTicks(), false);
    }
}
