package com.petrickfaria.betterhardcore.client;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "betterhardcore", bus = Mod.EventBusSubscriber.Bus.FORGE, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    public static void resetSelectionWhenOpeningWorldCreation(ScreenEvent.Opening event) {
        if (event.getNewScreen() instanceof CreateWorldScreen) SoftcoreSelection.setSelected(false);
    }

    @SubscribeEvent
    public static void replaceVanillaModeChoice(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof CreateWorldScreen screen)) return;

        CreateWorldScreenAccess access = (CreateWorldScreenAccess) screen;
        CycleButton<?> vanillaButton = access.betterhardcore$getModeButton();
        if (vanillaButton == null) return;

        String currentVanillaMode = vanillaButton.getValue() instanceof Enum<?> mode ? mode.name() : "SURVIVAL";
        WorldModeChoice initial = SoftcoreSelection.isSelected()
                ? WorldModeChoice.SOFTCORE
                : WorldModeChoice.fromVanillaName(currentVanillaMode);

        event.removeListener(vanillaButton);
        CycleButton<WorldModeChoice> integratedButton = CycleButton.<WorldModeChoice>builder(
                        choice -> Component.translatable(choice.displayKey()))
                .withValues(WorldModeChoice.values())
                .withInitialValue(initial)
                .withTooltip(choice -> choice.isSoftcore()
                        ? Minecraft.getInstance().font.split(
                                Component.translatable("betterhardcore.mode.softcore.description"), 240)
                        : List.of())
                .create(vanillaButton.x, vanillaButton.y, vanillaButton.getWidth(), vanillaButton.getHeight(),
                        Component.translatable("selectWorld.gameMode"),
                        (button, choice) -> applyChoice(screen, choice));
        event.addListener(integratedButton);
        applyChoice(screen, initial);
    }

    @SubscribeEvent
    public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientWorldState.setSoftcore(false);
    }

    private static void applyChoice(CreateWorldScreen screen, WorldModeChoice choice) {
        SoftcoreSelection.setSelected(choice.isSoftcore());
        screen.hardCore = choice == WorldModeChoice.HARDCORE;
        setVanillaGameMode(screen, choice.vanillaGameMode());
        ((CreateWorldScreenAccess) screen).betterhardcore$refreshModeHelp(choice);
    }

    /**
     * SelectedGameMode is private and cannot be extended safely. Resolve its vanilla enum value
     * by the field's runtime type, while the separate SoftcoreSelection preserves world identity.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setVanillaGameMode(CreateWorldScreen screen, String modeName) {
        try {
            Field modeField = Arrays.stream(CreateWorldScreen.class.getDeclaredFields())
                    .filter(field -> field.getType().isEnum()
                            && field.getType().getEnclosingClass() == CreateWorldScreen.class)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("CreateWorldScreen.SelectedGameMode field was not found"));
            modeField.setAccessible(true);
            Class<? extends Enum> enumType = (Class<? extends Enum>) modeField.getType();
            Object value = Enum.valueOf(enumType, modeName);
            modeField.set(screen, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not apply selected Better Hardcore game mode", exception);
        }
    }
}
