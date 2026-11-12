package com.petrickfaria.betterhardcore.mixin;

import com.petrickfaria.betterhardcore.client.CreateWorldScreenAccess;
import com.petrickfaria.betterhardcore.client.WorldModeChoice;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Exposes only the existing mode button and its existing help text to the replacement cycle button. */
@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin implements CreateWorldScreenAccess {
    @Shadow private CycleButton<?> modeButton;
    @Shadow private Component gameModeHelp1;
    @Shadow private Component gameModeHelp2;
    @Shadow private void updateGameModeHelp() {}

    @Override
    public CycleButton<?> betterhardcore$getModeButton() {
        return modeButton;
    }

    @Override
    public void betterhardcore$refreshModeHelp(WorldModeChoice choice) {
        updateGameModeHelp();
        if (choice.isSoftcore()) {
            gameModeHelp1 = Component.translatable("betterhardcore.mode.softcore.line1");
            gameModeHelp2 = Component.translatable("betterhardcore.mode.softcore.line2");
        }
    }

    @Override
    public void betterhardcore$setModeHelp(Component firstLine, Component secondLine) {
        gameModeHelp1 = firstLine;
        gameModeHelp2 = secondLine;
    }
}
