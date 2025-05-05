package org.intenses.insanitymod.panic;

import croissantnova.sanitydim.capability.SanityProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.utils.ModAttributes;


@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PanicEventHandler {
    private static final int DEEP_LEVEL_Y = 60;
    private static final int PANIC_INCREASE_DELAY = 60; // 3 секунды (60 тиков)
    private static final int PANIC_DECREASE_DELAY = 20; // 1 секунда (20 тиков)
    private static boolean blindnessApplied = false;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level.isClientSide()) return;

        Player player = event.player;
        AttributeInstance panicAttr = player.getAttribute(PanicAttributes.PANIC.get());
        AttributeInstance maxPanicAttr = player.getAttribute(PanicAttributes.MAX_PANIC.get());

        if (panicAttr == null || maxPanicAttr == null) return;

        double panic = PanicAttributes.clientPanic;
        double maxPanic = maxPanicAttr.getValue();
        boolean isUnderground = player.getY() < DEEP_LEVEL_Y &&
                !player.level.canSeeSky(player.blockPosition());

        CompoundTag data = player.getPersistentData();
        CompoundTag insanityTag = data.getCompound("insanitymod_panic");
        if (isUnderground) {
            int increaseTick = insanityTag.getInt("panic_increase_tick") + 1;
            if (increaseTick >= PANIC_INCREASE_DELAY) {
                increaseTick = 0;
                double next = panic + maxPanic * 0.01;
                if (next <= maxPanic) {
                    PanicSystem.setCurrentPanic(player, next);
                } else {
                    PanicSystem.setCurrentPanic(player, maxPanic);
                }
            }
            insanityTag.putInt("panic_increase_tick", increaseTick);
            insanityTag.putInt("panic_decrease_tick", 0);
        } else {
            int decreaseTick = insanityTag.getInt("panic_decrease_tick") + 1;
            if (decreaseTick >= PANIC_DECREASE_DELAY) {
                decreaseTick = 0;
                if (panic > 0) {
                    PanicSystem.setCurrentPanic(player, panic - maxPanic * 0.01);
                }
            }
            insanityTag.putInt("panic_decrease_tick", decreaseTick);
            insanityTag.putInt("panic_increase_tick", 0);
        }

        if (panic >= maxPanic) {
            if (!blindnessApplied) {
                float before = getSanity(player);
                Insanitymod.LOGGER.info("Sanity before reduction: " + before);
                reduceSanityByPercent(player, 65.0F);
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 1800, 0));
                blindnessApplied = true;
            }
        } else {
            blindnessApplied = false;
        }

        data.put("insanitymod_panic", insanityTag);
    }

    private static float getSanity(Player player) {
        final float[] sanityValue = new float[1];
        player.getCapability(SanityProvider.CAP).ifPresent(sanity -> {
            sanityValue[0] = sanity.getSanity();
        });
        return sanityValue[0];
    }

    private static void reduceSanityByPercent(Player player, float percent) {
        player.getCapability(SanityProvider.CAP).ifPresent(sanity -> {
            float current = sanity.getSanity();
            float loss = percent / 100f;
            float newSanity = current + loss;
            sanity.setSanity(newSanity);
        });
    }

    //TODO: сделать чтобы когда спавнились мобы ломались все блоки по тегу
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PanicAttributes.clientPanic = 0.0f;
    }
}
