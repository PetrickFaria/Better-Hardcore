package com.petrickfaria.betterhardcore.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "betterhardcore", bus = Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void addSoftcoreChoice(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof CreateWorldScreen screen)) return;
        int x = screen.width / 2 - 100;
        int y = screen.height - 56;
        Button button = new Button(x, y, 200, 20, label(), b -> {
            SoftcoreSelection.setSelected(!SoftcoreSelection.isSelected());
            b.setMessage(label());
        }, (b, poseStack, mouseX, mouseY) -> screen.renderTooltip(poseStack,
                Component.literal("Softcore: 3 hearts, survival-based growth, and a heart lost on death."), mouseX, mouseY));
        event.addListener(button);
    }

    @SubscribeEvent
    public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientWorldState.setSoftcore(false);
    }

    private static Component label() {
        return Component.literal(SoftcoreSelection.isSelected() ? "Softcore: ON" : "Softcore: OFF");
    }
}
