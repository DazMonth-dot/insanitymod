package org.intenses.insanitymod.panic;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.QoL.GlobalTumblers;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, value = Dist.CLIENT)
public class PanicSoundManager {

    // Таймеры
    private static final String TIMER_CAVE = "Panic_CaveTimer";
    private static final String TIMER_MISC = "Panic_MiscTimer";

    // Шаги
    private static final String STEPS_REMAINING = "Panic_StepsRemaining";
    private static final String STEPS_TIMER = "Panic_StepsTickTimer";
    private static final String STEPS_IS_BEHIND = "Panic_StepsBehind";

    private static final RandomSource rand = RandomSource.create();

    private static final String[] CAVE_SOUNDS = {
            "cave.cave1", "cave.cave2", "cave.cave4", "cave.cave10",
            "cave.cave11", "cave.cave12", "cave.cave13", "cave.cave14",
            "cave.cave16", "cave.cave17", "cave.cave19"
    };

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        if (!GlobalTumblers.PanicWork) return;

        if (event.getSound().getSource() == SoundSource.MUSIC) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;

            double panic = player.getAttributeValue(PanicAttributes.PANIC.get());
            double maxPanic = player.getAttributeValue(PanicAttributes.MAX_PANIC.get());

            if (maxPanic > 0 && panic / maxPanic >= 0.75) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!GlobalTumblers.PanicWork) return;
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        Level level = player.level;
        var data = player.getPersistentData();

        AttributeInstance panicAttr = player.getAttribute(PanicAttributes.PANIC.get());
        AttributeInstance maxAttr = player.getAttribute(PanicAttributes.MAX_PANIC.get());
        if (panicAttr == null || maxAttr == null) return;

        double panic = panicAttr.getValue();
        double max = maxAttr.getValue();
        int stage = calculateStage(panic, max);
        if (stage == 0) return;

        // ---- Звуки по стадиям ----
        tryPlaySound(player, TIMER_CAVE, 600, 1200, () -> {
            if (stage >= 2) playCaveAmbienceNearby(player, rand);
        });

        tryPlaySound(player, "Panic_StepTimer", 400, 800, () -> {
            if (stage >= 1) playFootstepsAround(player, rand);
        });

        tryPlaySound(player, TIMER_MISC, 800, 1600, () -> {
            if (stage == 3) {
                switch (rand.nextInt(3)) {
                    case 0 -> playBlockNoiseNearby(player);
                    case 1 -> playDragonGrowl(player);
                    case 2 -> playDisc11(player);
                }
            }
        });

        // Отдельная логика прогресса шагов (обновляется по своему таймеру)
        processPendingFootsteps(player);
    }

    private static int calculateStage(double current, double max) {
        if (max <= 0) return 0;
        double ratio = current / max;
        if (ratio >= 0.75) return 3;
        if (ratio >= 0.5) return 2;
        if (ratio >= 0.25) return 1;
        return 0;
    }

    private static void tryPlaySound(Player player, String tag, int minDelay, int maxDelay, Runnable action) {
        var data = player.getPersistentData();
        int timer = data.getInt(tag);
        if (timer <= 0) {
            action.run();
            int next = minDelay + rand.nextInt(maxDelay - minDelay + 1);
            data.putInt(tag, next);
        } else {
            data.putInt(tag, timer - 1);
        }
    }

    private static void playCaveAmbienceNearby(Player player, RandomSource rand) {
        String id = CAVE_SOUNDS[rand.nextInt(CAVE_SOUNDS.length)];
        SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(Insanitymod.MOD_ID, id));
        if (sound == null) return;

        double x = player.getX() + (rand.nextFloat() - 0.5) * 6;
        double y = player.getY();
        double z = player.getZ() + (rand.nextFloat() - 0.5) * 6;

        player.level.playSound(null, x, y, z, sound, SoundSource.AMBIENT,
                0.6F + rand.nextFloat() * 0.4F,
                0.9F + rand.nextFloat() * 0.2F);
    }

    private static void playBlockNoiseNearby(Player player) {
        SoundEvent[] sounds = {SoundEvents.STONE_PLACE, SoundEvents.STONE_BREAK};
        int count = 3 + rand.nextInt(3);

        for (int i = 0; i < count; i++) {
            double[] offset = getRandomOffset(player, rand, 1.5, 4.0, 0.3f);
            double x = player.getX() + offset[0];
            double y = player.getY();
            double z = player.getZ() + offset[1];

            SoundEvent sound = sounds[rand.nextInt(sounds.length)];
            player.level.playSound(null, x, y, z, sound, SoundSource.AMBIENT,
                    0.8F + rand.nextFloat() * 0.4F,
                    0.9F + rand.nextFloat() * 0.3F);
        }
    }

    private static void playDragonGrowl(Player player) {
        double[] offset = getRandomOffset(player, rand, 20.0, 40.0, 0.1f);
        double x = player.getX() + offset[0];
        double y = player.getY();
        double z = player.getZ() + offset[1];

        player.level.playSound(null, x, y, z,
                SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE,
                4.0F, 0.8F + rand.nextFloat() * 0.2F);
    }

    private static void playDisc11(Player player) {
        SoundEvent disc = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft", "music_disc.11"));
        if (disc != null) {
            player.level.playSound(null, player.getX(), player.getY() + 1.0f, player.getZ(),
                    disc, SoundSource.RECORDS, 1.0F, 1.0F);
        }
    }
    private static void processPendingFootsteps(Player player) {
        var data = player.getPersistentData();
        int remaining = data.getInt(STEPS_REMAINING);
        if (remaining <= 0) return;

        int timer = data.getInt(STEPS_TIMER);
        if (timer > 0) {
            data.putInt(STEPS_TIMER, timer - 1);
            return;
        }

        RandomSource rand = player.getRandom();
        boolean isBehind = data.getBoolean(STEPS_IS_BEHIND);
        double progress = (5.0 - remaining) / 5.0;
        double minRadius = isBehind ? 6.0 - progress * 5.0 : 1.5;
        double maxRadius = isBehind ? 6.5 - progress * 5.0 : 4.0;

        double[] offset = getRandomOffset(player, rand, minRadius, maxRadius, isBehind ? 1.0f : 0f);
        double x = player.getX() + offset[0];
        double y = player.getY() + rand.nextFloat() * 0.3;
        double z = player.getZ() + offset[1];

        SoundEvent[] sounds = {SoundEvents.STONE_STEP, SoundEvents.DEEPSLATE_STEP};
        SoundEvent sound = sounds[rand.nextInt(sounds.length)];

        player.level.playSound(null, x, y, z, sound, SoundSource.AMBIENT,
                0.7F + rand.nextFloat() * 0.3F,
                0.85F + rand.nextFloat() * 0.2F);

        data.putInt(STEPS_REMAINING, remaining - 1);
        data.putInt(STEPS_TIMER, 5); // задержка между шагами
    }

    private static void playFootstepsAround(Player player, RandomSource rand) {
        player.getPersistentData().putInt(STEPS_REMAINING, 5 + rand.nextInt(3)); // 5-7 шагов
        player.getPersistentData().putInt(STEPS_TIMER, 0);
        player.getPersistentData().putBoolean(STEPS_IS_BEHIND, rand.nextFloat() < 0.5f);
    }

    private static double[] getRandomStepOffset(float yaw, double radius) {
        double rad = Math.toRadians(yaw);
        double lookX = -Math.sin(rad) * radius;
        double lookZ = Math.cos(rad) * radius;
        double x = lookX + rand.nextGaussian() * 0.5;
        double z = lookZ + rand.nextGaussian() * 0.5;
        return new double[]{x, z};
    }

    private static double[] getRandomOffset(Player player, RandomSource rand, double min, double max, float backChance) {
        double angle = rand.nextDouble() * Math.PI * 2;
        double radius = min + rand.nextDouble() * (max - min);
        double x = Math.cos(angle) * radius;
        double z = Math.sin(angle) * radius;

        if (rand.nextFloat() < backChance) {
            double lookX = -player.getLookAngle().x * radius;
            double lookZ = -player.getLookAngle().z * radius;
            x = lookX + rand.nextGaussian() * 0.5;
            z = lookZ + rand.nextGaussian() * 0.5;
        }

        return new double[]{x, z};
    }
}
