package org.intenses.insanitymod.panic;

import croissantnova.sanitydim.capability.SanityProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.QoL.GlobalTumblers;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PanicEventHandler {
    private static final int DEEP_LEVEL_Y = 60;
    private static final int PANIC_INCREASE_DELAY = 60; // 3 секунды (60 тиков)
    private static final int PANIC_DECREASE_DELAY = 20; // 1 секунда (20 тиков)
    private static final String TAG_INCREASE = "panic_increase_tick";
    private static final String TAG_DECREASE = "panic_decrease_tick";
    private static final String TAG_MAX_REACHED = "panic_max_reached";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level.isClientSide()) return;
        if (!GlobalTumblers.PanicWork) return;

        Player player = event.player;
        AttributeInstance panicAttr = player.getAttribute(PanicAttributes.PANIC.get());
        AttributeInstance maxAttr = player.getAttribute(PanicAttributes.MAX_PANIC.get());
        if (panicAttr == null || maxAttr == null) return;

        double panic = panicAttr.getValue();
        double maxPanic = maxAttr.getValue();

        CompoundTag data = player.getPersistentData();
        CompoundTag tag = data.getCompound("insanitymod_panic");

        // — Регулировка паники ↑↓ —
        if (player.getY() < DEEP_LEVEL_Y) {
            int inc = tag.getInt(TAG_INCREASE) + 1;
            if (inc >= PANIC_INCREASE_DELAY) {
                PanicSystem.setCurrentPanic(player, Math.min(maxPanic, panic + maxPanic * 0.01));
                inc = 0;
            }
            tag.putInt(TAG_INCREASE, inc);
            tag.putInt(TAG_DECREASE, 0);
        } else {
            int dec = tag.getInt(TAG_DECREASE) + 1;
            if (dec >= PANIC_DECREASE_DELAY) {
                PanicSystem.setCurrentPanic(player, Math.max(0, panic - maxPanic * 0.01));
                dec = 0;
            }
            tag.putInt(TAG_DECREASE, dec);
            tag.putInt(TAG_INCREASE, 0);
        }

        // — Эффекты при достижении 100% паники один раз —
        boolean maxReached = tag.getBoolean(TAG_MAX_REACHED);
        if (panic >= maxPanic) {
            if (!maxReached) {
                // первый раз при достижении 100%
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 1800, 0, false, false));
                reduceSanityByPercent(player, 65.0F);
                tag.putBoolean(TAG_MAX_REACHED, true);
            }
        } else {
            // паника упала — сброс флага
            tag.putBoolean(TAG_MAX_REACHED, false);
        }

        data.put("insanitymod_panic", tag);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!GlobalTumblers.PanicWork) return;
        if (event.getEntity() instanceof Player) {
            Player player = event.getEntity();
            PanicSystem.setCurrentPanic(player, 0.0);
            CompoundTag data = player.getPersistentData().getCompound("insanitymod_panic");
            data.putBoolean(TAG_MAX_REACHED, false);
            player.getPersistentData().put("insanitymod_panic", data);
        }
    }

    private static void reduceSanityByPercent(Player player, float percent) {
        player.getCapability(SanityProvider.CAP).ifPresent(sanity -> {
            float current = sanity.getSanity();
            float delta = current * percent / 100f;
            sanity.setSanity(Math.max(0, current - delta));
        });
    }
}
