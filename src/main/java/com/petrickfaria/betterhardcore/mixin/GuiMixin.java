package com.petrickfaria.betterhardcore.mixin;

import com.petrickfaria.betterhardcore.client.ClientWorldState;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.level.storage.LevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Reuses vanilla's hardcore variants of the full and half-heart sprites in Softcore HUDs. */
@Mixin(Gui.class)
public abstract class GuiMixin {
    @Redirect(method = "renderHearts", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/storage/LevelData;isHardcore()Z"))
    private boolean betterhardcore$useHardcoreHeartSprites(LevelData levelData) {
        return levelData.isHardcore() || ClientWorldState.isSoftcore();
    }
}
