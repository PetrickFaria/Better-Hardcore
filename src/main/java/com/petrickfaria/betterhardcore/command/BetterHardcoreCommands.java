package com.petrickfaria.betterhardcore.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.petrickfaria.betterhardcore.data.BetterHardcoreData;
import com.petrickfaria.betterhardcore.data.WorldMode;
import com.petrickfaria.betterhardcore.event.ServerEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class BetterHardcoreCommands {
    private BetterHardcoreCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("betterhardcore")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("mode")
                        .then(Commands.argument("mode", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    builder.suggest("softcore"); builder.suggest("hardcore"); builder.suggest("vanilla");
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String value = StringArgumentType.getString(context, "mode").toUpperCase();
                                    WorldMode mode;
                                    try { mode = WorldMode.valueOf(value); }
                                    catch (IllegalArgumentException ex) {
                                        context.getSource().sendFailure(Component.literal("Mode must be softcore, hardcore, or vanilla."));
                                        return 0;
                                    }
                                    if (mode == WorldMode.HARDCORE && !context.getSource().getServer().getWorldData().isHardcore()) {
                                        context.getSource().sendFailure(Component.literal("Hardcore must be selected when the world is created; it cannot be enabled safely after creation."));
                                        return 0;
                                    }
                                    if (context.getSource().getServer().getWorldData().isHardcore() && mode != WorldMode.HARDCORE) {
                                        context.getSource().sendFailure(Component.literal("Vanilla Hardcore worlds cannot switch to another Better Hardcore mode."));
                                        return 0;
                                    }
                                    BetterHardcoreData.get(context.getSource().getServer()).setMode(mode);
                                    ServerEvents.refreshPlayers(context.getSource().getServer());
                                    context.getSource().sendSuccess(Component.literal("Better Hardcore mode set to " + mode.name().toLowerCase() + "."), true);
                                    return 1;
                                }))));
    }
}
