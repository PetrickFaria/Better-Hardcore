package com.petrickfaria.betterhardcore.client;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WorldModeChoiceTest {
    @Test
    void choicesFollowTheRequestedExclusiveCycleOrder() {
        assertArrayEquals(new WorldModeChoice[]{
                WorldModeChoice.SURVIVAL,
                WorldModeChoice.CREATIVE,
                WorldModeChoice.HARDCORE,
                WorldModeChoice.SOFTCORE
        }, WorldModeChoice.values());
    }

    @Test
    void softcoreUsesSurvivalInternallyWithoutCombiningModes() {
        assertEquals("SURVIVAL", WorldModeChoice.SOFTCORE.vanillaGameMode());
        assertTrue(WorldModeChoice.SOFTCORE.isSoftcore());
        assertFalse(WorldModeChoice.SURVIVAL.isSoftcore());
        assertFalse(WorldModeChoice.CREATIVE.isSoftcore());
        assertFalse(WorldModeChoice.HARDCORE.isSoftcore());
    }
}
