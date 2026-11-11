package com.petrickfaria.betterhardcore.network;

import com.petrickfaria.betterhardcore.BetterHardcore;
import com.petrickfaria.betterhardcore.data.WorldMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class NetworkHandler {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BetterHardcore.MOD_ID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
    private static boolean registered;

    private NetworkHandler() {}

    public static void register() {
        if (registered) return;
        CHANNEL.registerMessage(0, ModeSyncPacket.class, ModeSyncPacket::encode, ModeSyncPacket::decode, ModeSyncPacket::handle);
        registered = true;
    }

    public static void sync(ServerPlayer player, WorldMode mode) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ModeSyncPacket(mode == WorldMode.SOFTCORE));
    }
}
