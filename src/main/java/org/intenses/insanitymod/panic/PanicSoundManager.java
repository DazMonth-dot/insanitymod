package org.intenses.insanitymod.panic;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.intenses.insanitymod.Insanitymod;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PanicSoundManager {
    private static final String TIMER_CAVE = "Panic_CaveTimer";
    private static final String TIMER_STEPS = "Panic_StepTimer";
    private static final String TIMER_MISC = "Panic_MiscTimer";


    private static final String STEPS_REMAINING = "Panic_StepsRemaining";
    private static final String STEPS_TIMER = "Panic_StepsTickTimer";
    private static final String STEPS_IS_BEHIND = "Panic_StepsBehind";


    private static final String[] CUSTOM_CAVE_SOUNDS = {
            "cave.cave1", "cave.cave2", "cave.cave4", "cave.cave10",
            "cave.cave11", "cave.cave12", "cave.cave13", "cave.cave14",
            "cave.cave16", "cave.cave17", "cave.cave19"
    };

    private static final SoundEvent[] REGISTERED_CAVE_SOUNDS = new SoundEvent[CUSTOM_CAVE_SOUNDS.length];
    private static boolean caveSoundsInitialized = false;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        if (player.level.isClientSide()) return;

        processPendingFootsteps(player);

        double panic = getPanicLevel(player);
        int stage = calculatePanicStage(panic);
        if (stage == 0) return;

        RandomSource rand = player.getRandom();

        tryPlaySound(player, TIMER_CAVE, 600, 1200, () -> {
            if (stage >= 2) playCaveAmbienceNearby(player, rand);
        });

        tryPlaySound(player, TIMER_STEPS, 400, 800, () -> {
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
    }

    @SubscribeEvent
    public static void onSoundPlay(PlaySoundEvent event) {
        if (event.getSound().getSource() == SoundSource.MUSIC) {
            Player player = Minecraft.getInstance().player;

            if (player == null) {
                return;
            }
            double panic = getPanicLevel(player);
            int stage = calculatePanicStage(panic);

            if (stage >= 2) {
                event.setCanceled(true);
                //sendDebugMessage(player, "Звук музыки заблокирован из-за паники");
            }
        }
    }



    ///**
    /// ambient cave cave1
    /// cave 10
    /// cave 11
    /// cave 12
    /// cave 13
    /// cave 14
    /// cave 16
    /// cave 17
    /// cave 19
    /// cave 2
    /// cave 4

    private static double getPanicLevel(Player player) {
        AttributeInstance attr = player.getAttribute(PanicAttributes.PANIC.get());
        return attr != null ? attr.getValue() : 0;
    }

    private static int calculatePanicStage(double panic) {
        if (panic >= 75) return 3;
        if (panic >= 50) return 2;
        if (panic >= 25) return 1;
        return 0;
    }

    private static void tryPlaySound(Player player, String tag, int minDelay, int maxDelay, Runnable action) {
        int timer = player.getPersistentData().getInt(tag);
        if (timer <= 0) {
            action.run();
            int next = minDelay + player.getRandom().nextInt(maxDelay - minDelay + 1);
            player.getPersistentData().putInt(tag, next);
        } else {
            player.getPersistentData().putInt(tag, timer - 1);
        }
    }

    private static void playFootstepsAround(Player player, RandomSource rand) {
        player.getPersistentData().putInt(STEPS_REMAINING, 5 + rand.nextInt(3)); // 5-7 шагов
        player.getPersistentData().putInt(STEPS_TIMER, 0);
        player.getPersistentData().putBoolean(STEPS_IS_BEHIND, rand.nextFloat() < 0.5f); // 50% шанс что сзади

        //sendDebugMessage(player, "Начало шагов рядом");
    }
    private static void processPendingFootsteps(Player player) {
        if (player.level.isClientSide()) return;

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
        double progress = (5.0 - remaining) / 5.0; // От 0 до 1 — шаги приближаются
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
        data.putInt(STEPS_TIMER, 5); // 5 тиков задержка = ~1/4 секунды

        if (remaining - 1 == 0) {
           // sendDebugMessage(player, "Шаги закончились");
        }
    }

    private static void playBlockNoiseNearby(Player player) {
        RandomSource rand = player.getRandom();
        int count = 3 + rand.nextInt(3); // 3–5 звуков

        SoundEvent[] sounds = {SoundEvents.STONE_PLACE, SoundEvents.STONE_BREAK};

        for (int i = 0; i <= count; i++) {
            double[] offset = getRandomOffset(player, rand, 1.5, 4.0, 0.3f);
            double x = player.getX() + offset[0];
            double y = player.getY();
            double z = player.getZ() + offset[1];

            SoundEvent sound = sounds[rand.nextInt(sounds.length)];

            player.level.playSound(null, x, y, z, sound, SoundSource.AMBIENT,
                    0.8F + rand.nextFloat() * 0.4F,
                    0.9F + rand.nextFloat() * 0.3F);
        }

        //sendDebugMessage(player, "Звук: блоки рядом");
    }

    private static void playCaveAmbienceNearby(Player player, RandomSource rand) {
        if (!caveSoundsInitialized) {
            for (int i = 0; i < CUSTOM_CAVE_SOUNDS.length; i++) {
                ResourceLocation id = new ResourceLocation(Insanitymod.MOD_ID, CUSTOM_CAVE_SOUNDS[i]);
                SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(id);
                REGISTERED_CAVE_SOUNDS[i] = sound;
            }
            caveSoundsInitialized = true;
        }

        SoundEvent selected = REGISTERED_CAVE_SOUNDS[rand.nextInt(REGISTERED_CAVE_SOUNDS.length)];
        if (selected == null) return;

        double x = player.getX() + (rand.nextFloat() - 0.5) * 6;
        double y = player.getY();
        double z = player.getZ() + (rand.nextFloat() - 0.5) * 6;

        player.level.playSound(null, x, y, z, selected, SoundSource.AMBIENT,
                0.6F + rand.nextFloat() * 0.4F,
                0.9F + rand.nextFloat() * 0.2F);

       // sendDebugMessage(player, "Звук: пещера рядом (" + selected.getLocation() + ")");
    }

    private static void playDragonGrowl(Player player) {
        RandomSource rand = player.getRandom();
        double[] offset = getRandomOffset(player, rand, 20.0, 40.0, 0.1f); // теперь далеко
        double x = player.getX() + offset[0];
        double y = player.getY();
        double z = player.getZ() + offset[1];

        player.level.playSound(null, x, y, z,
                SoundEvents.ENDER_DRAGON_GROWL, SoundSource.HOSTILE,
                4.0F, 0.8F + rand.nextFloat() * 0.2F);

        player.gameEvent(GameEvent.ENTITY_ROAR);
        //sendDebugMessage(player, "Звук: дракон вдали");
    }


    private static void playDisc11(Player player) {
        SoundEvent disc = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("minecraft", "music_disc.11"));
        if (disc != null) {
            player.level.playSound(null, player.getX(), player.getY()+1.0f, player.getZ(), disc,
                    SoundSource.RECORDS, 1.0F, 1.0F);
           // sendDebugMessage(player, "Звук: пластинка 11");
        }
    }

    private static double[] getRandomOffset(Player player, RandomSource rand, double minRadius, double maxRadius, float backChance) {
        double angle = rand.nextDouble() * Math.PI * 2;
        double radius = minRadius + rand.nextDouble() * (maxRadius - minRadius);
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

//    private static void sendDebugMessage(Player player, String message) {
//        if (player instanceof ServerPlayer serverPlayer) {
//            serverPlayer.sendSystemMessage(Component.literal("[PANIC] " + message));
//            Insanitymod.LOGGER.info("[PANIC] " + message);
//        }
//    }


}
