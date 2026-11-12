package com.petrickfaria.betterhardcore.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ModeSyncPacket(boolean softcore) {
    public static void encode(ModeSyncPacket packet, FriendlyByteBuf buffer) { buffer.writeBoolean(packet.softcore); }
    public static ModeSyncPacket decode(FriendlyByteBuf buffer) { return new ModeSyncPacket(buffer.readBoolean()); }
    public static void handle(ModeSyncPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(
                net.minecraftforge.api.distmarker.Dist.CLIENT,
                () -> () -> com.petrickfaria.betterhardcore.client.ClientWorldState.setSoftcore(packet.softcore)));
        context.setPacketHandled(true);
    }
}
