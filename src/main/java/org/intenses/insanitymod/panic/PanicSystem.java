package org.intenses.insanitymod.panic;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.intenses.insanitymod.panic.PanicAttributes;

public class PanicSystem {
    public static double getCurrentPanic(Player player) {
        AttributeInstance attr = player.getAttribute(PanicAttributes.PANIC.get());
        return attr != null ? attr.getValue() : 0;
    }

    public static double getMaxPanic(Player player) {
        AttributeInstance attr = player.getAttribute(PanicAttributes.MAX_PANIC.get());
        return attr != null ? attr.getValue() : 100.0;
    }

    public static void setCurrentPanic(Player player, double value) {
        AttributeInstance panicAttr = player.getAttribute(PanicAttributes.PANIC.get());
        AttributeInstance maxPanicAttr = player.getAttribute(PanicAttributes.MAX_PANIC.get());
        if (panicAttr == null || maxPanicAttr == null) return;
        double max = maxPanicAttr.getValue();
        double clamped = Mth.clamp(value, 0, max);
        panicAttr.setBaseValue(clamped);
    }
}