package com.petrickfaria.betterhardcore.data;

import com.petrickfaria.betterhardcore.core.SoftcoreRules;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BetterHardcoreDataTest {
    @Test void worldModeAndPlayerStatePersistIndependently() {
        BetterHardcoreData data = new BetterHardcoreData();
        data.setMode(WorldMode.SOFTCORE);
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        data.setProgress(first, new SoftcoreRules.Progress(17, 12345, false));
        data.setProgress(second, new SoftcoreRules.Progress(8, 360, true));

        CompoundTag saved = data.save(new CompoundTag());
        BetterHardcoreData loaded = BetterHardcoreData.load(saved);

        assertEquals(WorldMode.SOFTCORE, loaded.mode());
        assertEquals(new SoftcoreRules.Progress(17, 12345, false), loaded.progress(first));
        assertEquals(new SoftcoreRules.Progress(8, 360, true), loaded.progress(second));
        assertNotEquals(loaded.progress(first), loaded.progress(second));
    }

    @Test void vanillaWorldDefaultsToNoSoftcoreProgress() {
        BetterHardcoreData loaded = BetterHardcoreData.load(new CompoundTag());
        assertEquals(WorldMode.VANILLA, loaded.mode());
        assertFalse(loaded.hasProgress(UUID.randomUUID()));
    }
}
