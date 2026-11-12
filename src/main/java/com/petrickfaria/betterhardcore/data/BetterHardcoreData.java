package com.petrickfaria.betterhardcore.data;

import com.petrickfaria.betterhardcore.core.SoftcoreRules;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** World mode plus per-player softcore state, stored in the overworld's level data. */
public final class BetterHardcoreData extends SavedData {
    private static final String KEY = "betterhardcore";
    private WorldMode mode = WorldMode.VANILLA;
    private final Map<UUID, SoftcoreRules.Progress> players = new HashMap<>();

    public static BetterHardcoreData get(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(BetterHardcoreData::load, BetterHardcoreData::new, KEY);
    }

    public WorldMode mode() { return mode; }
    public void setMode(WorldMode value) { if (mode != value) { mode = value; setDirty(); } }
    public SoftcoreRules.Progress progress(UUID id) { return players.getOrDefault(id, new SoftcoreRules.Progress(SoftcoreRules.START_HEALTH, 0L, false)); }
    public boolean hasProgress(UUID id) { return players.containsKey(id); }
    public void setProgress(UUID id, SoftcoreRules.Progress value) { players.put(id, value); setDirty(); }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putString("mode", mode.name());
        ListTag list = new ListTag();
        players.forEach((id, p) -> {
            CompoundTag item = new CompoundTag();
            item.putUUID("uuid", id);
            item.putInt("maxHealth", p.maxHealth());
            item.putLong("survivedTicks", p.survivedTicks());
            item.putBoolean("permanentlyDead", p.permanentlyDead());
            list.add(item);
        });
        tag.put("players", list);
        return tag;
    }

    public static BetterHardcoreData load(CompoundTag tag) {
        BetterHardcoreData data = new BetterHardcoreData();
        data.mode = WorldMode.parse(tag.getString("mode"));
        ListTag list = tag.getList("players", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag item = list.getCompound(i);
            if (!item.hasUUID("uuid")) continue;
            int max = Math.max(SoftcoreRules.MINIMUM_HEALTH, Math.min(SoftcoreRules.MAX_HEALTH, item.getInt("maxHealth")));
            long ticks = Math.max(0L, Math.min(SoftcoreRules.TICKS_PER_REWARD - 1, item.getLong("survivedTicks")));
            data.players.put(item.getUUID("uuid"), new SoftcoreRules.Progress(max, ticks, item.getBoolean("permanentlyDead")));
        }
        return data;
    }
}
