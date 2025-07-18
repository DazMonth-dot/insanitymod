package org.intenses.insanitymod.panic;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.intenses.insanitymod.Insanitymod;

public class PanicCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
//        if (!StressBarRenderer.WORKING) return;
        dispatcher.register(Commands.literal("insanity")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("get")
                        .executes(ctx -> getPanic(ctx.getSource(), ctx.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ctx -> getPanic(ctx.getSource(), EntityArgument.getPlayer(ctx, "target"))))
                )
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                        .executes(ctx -> setPanic(ctx.getSource(), EntityArgument.getPlayer(ctx, "target"), DoubleArgumentType.getDouble(ctx, "value")))))));
    }

    private static int getPanic(CommandSourceStack source, ServerPlayer player) {
//        if (!StressBarRenderer.WORKING) return 0;
        AttributeInstance maxAttr = player.getAttribute(PanicAttributes.MAX_PANIC.get());
        if (maxAttr == null) return 0;
        double panic = player.getAttributeValue(PanicAttributes.PANIC.get());
        double maxPanic = maxAttr.getValue();
        int stage = calculateStage(panic, maxPanic);
        Component stageComponent = Component.translatable("insanity.panicscale.stage." + stage)
                .withStyle(getStageComponent(stage));
        source.sendSuccess(stageComponent, false);
        return 1;
    }

    private static int setPanic(CommandSourceStack source, ServerPlayer player, double value) {
        if (!checkGameMode(source, player)) return 0;
        PanicSystem.setCurrentPanic(player, value);
        double maxPanic = PanicSystem.getMaxPanic(player);
        int stage = calculateStage(value, maxPanic);
        source.sendSuccess(Component.translatable("insanity.panicscale.set")
                .append(Component.literal(stage+"").withStyle(getStageComponent(stage))), false);
        return 1;
    }

    private static boolean checkGameMode(CommandSourceStack source, ServerPlayer player) {
        if (!player.gameMode.isCreative() && !player.isSpectator()) {
            source.sendFailure(Component.translatable("insanity.panicscale.gamemode_error"));
            return false;
        }
        return true;
    }

    private static int calculateStage(double current, double max) {
        if (max <= 0) return 0;
        double ratio = current / max;
        if (ratio >= 0.75) return 3;
        if (ratio >= 0.50) return 2;
        if (ratio >= 0.25) return 1;
        return 0;
    }
    private static ChatFormatting getStageComponent(int stage) {
        return switch (stage) {
            case 1 -> ChatFormatting.YELLOW;
            case 2 -> ChatFormatting.GOLD;
            case 3 -> ChatFormatting.RED;
            default -> ChatFormatting.GREEN;
        };
    }
}