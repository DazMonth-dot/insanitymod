package org.intenses.insanitymod.panic;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.QoL.GlobalTumblers;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerHandler {
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (!GlobalTumblers.PanicWork) return;
        if (event.getEntity() instanceof Player player && !event.getLevel().isClientSide()) {
            initAttributes(player);
        }
    }

    private static void initAttributes(Player player) {
        AttributeInstance panic = player.getAttribute(PanicAttributes.PANIC.get());
        AttributeInstance maxPanic = player.getAttribute(PanicAttributes.MAX_PANIC.get());
        if (panic == null || maxPanic == null) {
            Insanitymod.LOGGER.error("Attributes missing! Check registration");
            return;
        }
        if (maxPanic.getBaseValue() < 10.0) {
            maxPanic.setBaseValue(100.0);
        }
        if (panic.getBaseValue() < 0.0) {
            panic.setBaseValue(0.0);
        }
    }
}