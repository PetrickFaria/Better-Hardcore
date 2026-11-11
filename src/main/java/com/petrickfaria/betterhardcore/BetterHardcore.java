package com.petrickfaria.betterhardcore;

import com.petrickfaria.betterhardcore.command.BetterHardcoreCommands;
import com.petrickfaria.betterhardcore.event.ServerEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(BetterHardcore.MOD_ID)
public final class BetterHardcore {
    public static final String MOD_ID = "betterhardcore";

    public BetterHardcore() {
        MinecraftForge.EVENT_BUS.register(ServerEvents.class);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        BetterHardcoreCommands.register(event.getDispatcher());
    }
}
