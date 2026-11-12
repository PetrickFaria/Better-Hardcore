package com.petrickfaria.betterhardcore.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        boolean includeDebug = "DEBUG".equals(currentVanillaMode) || vanillaOffersDebug(vanillaButton);
        WorldModeChoice initial = SoftcoreSelection.isSelected()
                ? WorldModeChoice.SOFTCORE
                : WorldModeChoice.fromVanillaName(currentVanillaMode);

        event.removeListener(vanillaButton);
        CycleButton<WorldModeChoice> integratedButton = CycleButton.<WorldModeChoice>builder(
                        choice -> Component.translatable(choice.displayKey()))
                .withValues(WorldModeChoice.choices(includeDebug))
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
        setVanillaGameMode(screen, choice.vanillaGameMode());
        ((CreateWorldScreenAccess) screen).betterhardcore$refreshModeHelp(choice);
    }

    private static boolean vanillaOffersDebug(CycleButton<?> button) {
        try {
            for (Field field : CycleButton.class.getDeclaredFields()) {
                if (!CycleButton.ValueListSupplier.class.isAssignableFrom(field.getType())) continue;
                field.setAccessible(true);
                CycleButton.ValueListSupplier<?> values = (CycleButton.ValueListSupplier<?>) field.get(button);
                return containsMode(values.getDefaultList(), "DEBUG")
                        || containsMode(values.getSelectedList(), "DEBUG");
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // A screen variant that does not expose the vanilla debug option keeps the four standard choices.
        }
        return false;
    }

    private static boolean containsMode(List<?> values, String name) {
        return values.stream().anyMatch(value -> value instanceof Enum<?> mode && name.equals(mode.name()));
    }

    /**
     * SelectedGameMode is private and cannot be extended safely. Resolve its vanilla enum value
     * by the field's runtime type and invoke vanilla's own setter, while Softcore identity stays
     * separate from GameType and exclusive through the single cycle button.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setVanillaGameMode(CreateWorldScreen screen, String modeName) {
        try {
            Field modeField = Arrays.stream(CreateWorldScreen.class.getDeclaredFields())
                    .filter(field -> field.getType().isEnum()
                            && field.getType().getEnclosingClass() == CreateWorldScreen.class)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("CreateWorldScreen.SelectedGameMode field was not found"));
            Class<? extends Enum> enumType = (Class<? extends Enum>) modeField.getType();
            Object value = Enum.valueOf(enumType, modeName);
            Method modeSetter = Arrays.stream(CreateWorldScreen.class.getDeclaredMethods())
                    .filter(method -> method.getReturnType() == void.class
                            && method.getParameterCount() == 1
                            && method.getParameterTypes()[0] == enumType)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("CreateWorldScreen game mode setter was not found"));
            modeSetter.setAccessible(true);
            modeSetter.invoke(screen, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Could not apply selected Better Hardcore game mode", exception);
        }
    }
}
