package com.petrickfaria.betterhardcore.event;

import com.petrickfaria.betterhardcore.client.SoftcoreSelection;
import com.petrickfaria.betterhardcore.core.SoftcoreRules;
import com.petrickfaria.betterhardcore.data.BetterHardcoreData;
import com.petrickfaria.betterhardcore.data.WorldMode;
import com.petrickfaria.betterhardcore.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public final class ServerEvents {
    private ServerEvents() {}

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        BetterHardcoreData data = BetterHardcoreData.get(event.getServer());
        if (data.mode() == WorldMode.VANILLA) {
            if (event.getServer().getWorldData().isHardcore()) data.setMode(WorldMode.HARDCORE);
            else if (SoftcoreSelection.consume()) data.setMode(WorldMode.SOFTCORE);
        } else {
            SoftcoreSelection.consume();
        }
        if (data.mode() == WorldMode.SOFTCORE) {
            event.getServer().setDefaultGameMode(net.minecraft.world.level.GameType.SURVIVAL);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        apply(event.getEntity());
        if (event.getEntity() instanceof ServerPlayer player) NetworkHandler.sync(player, BetterHardcoreData.get(player.getServer()).mode());
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) { apply(event.getEntity()); }

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) { apply(event.getEntity()); }

    private static void apply(net.minecraft.world.entity.player.Player entity) {
        if (!(entity instanceof ServerPlayer player)) return;
        BetterHardcoreData data = BetterHardcoreData.get(player.getServer());
        if (data.mode() == WorldMode.HARDCORE) {
            setMaxHealth(player, 14);
            player.setHealth(Math.min(player.getHealth(), 14.0F));
        } else if (data.mode() == WorldMode.SOFTCORE) {
            SoftcoreRules.Progress progress = data.progress(player.getUUID());
            if (!data.hasProgress(player.getUUID())) {
                data.setProgress(player.getUUID(), progress);
            }
            setMaxHealth(player, progress.maxHealth());
            if (progress.permanentlyDead()) player.setGameMode(net.minecraft.world.level.GameType.SPECTATOR);
            else if (player.isCreative()) player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
            else if (player.isDeadOrDying() || player.getHealth() <= 0.0F) player.setHealth(progress.maxHealth());
            else player.setHealth(Math.min(player.getHealth(), progress.maxHealth()));
        }
    }

    public static void refreshPlayers(net.minecraft.server.MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            apply(player);
            NetworkHandler.sync(player, BetterHardcoreData.get(server).mode());
        }
    }

    private static void setMaxHealth(ServerPlayer player, int value) {
        var attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute != null && Double.compare(attribute.getBaseValue(), value) != 0) {
            attribute.setBaseValue(value);
            if (player.getHealth() > value) player.setHealth(value);
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        BetterHardcoreData data = BetterHardcoreData.get(player.getServer());
        if (data.mode() != WorldMode.SOFTCORE) return;
        if (!data.hasProgress(player.getUUID())) data.setProgress(player.getUUID(), data.progress(player.getUUID()));
        data.setProgress(player.getUUID(), SoftcoreRules.die(data.progress(player.getUUID())));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) return;
        BetterHardcoreData data = BetterHardcoreData.get(player.getServer());
        if (data.mode() != WorldMode.SOFTCORE) return;
        if (!data.hasProgress(player.getUUID())) data.setProgress(player.getUUID(), data.progress(player.getUUID()));
        SoftcoreRules.Progress before = data.progress(player.getUUID());
        SoftcoreRules.Progress after = SoftcoreRules.tick(before, player.isAlive() && !player.isCreative() && !player.isSpectator());
        if (!before.equals(after)) {
            data.setProgress(player.getUUID(), after);
            if (after.maxHealth() != before.maxHealth()) {
                setMaxHealth(player, after.maxHealth());
                player.setHealth(Math.min(player.getHealth(), after.maxHealth()));
            }
        }
    }
}
