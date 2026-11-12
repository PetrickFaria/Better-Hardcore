package com.petrickfaria.betterhardcore.client;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;

/** Narrow bridge from the Forge screen event to the vanilla create-world screen state. */
public interface CreateWorldScreenAccess {
    CycleButton<?> betterhardcore$getModeButton();

    void betterhardcore$refreshModeHelp(WorldModeChoice choice);

    void betterhardcore$setModeHelp(Component firstLine, Component secondLine);
}
