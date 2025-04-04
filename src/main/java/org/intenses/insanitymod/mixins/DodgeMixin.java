package org.intenses.insanitymod.mixins;

import com.elenai.elenaidodge2.client.ED2ClientStorage;
import com.elenai.elenaidodge2.client.animation.DodgeAnimator;
import com.elenai.elenaidodge2.event.ClientEvents;
import com.elenai.elenaidodge2.networking.ED2Messages;
import com.elenai.elenaidodge2.networking.messages.DodgeEffectsCTSPacket;
import com.elenai.elenaidodge2.util.DodgeHandler;
import com.elenai.feathers.api.FeathersHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.intenses.insanitymod.Insanitymod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = DodgeHandler.class, remap = false)
public class DodgeMixin {

    @Unique
    private static void spawnParticles(Level level, Vec3 pos, int count) {
        if (level == null) {
            Insanitymod.LOGGER.warn("Level is null, cannot spawn particles");
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            // Серверный спавн для мультиплеера
            serverLevel.sendParticles(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, count, 0, 0, 0, 0);
            serverLevel.sendParticles(ParticleTypes.POOF, pos.x, pos.y, pos.z, count, 0, 0, 0, 0);
        } else {
            // Клиентский спавн как запасной вариант
            level.addParticle(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 0, 0, 0);
            level.addParticle(ParticleTypes.POOF, pos.x, pos.y, pos.z, 0, 0, 0);
            Insanitymod.LOGGER.debug("Spawned particles on client side at {}", pos);
        }
    }

    @Unique
    private static ServerPlayer getServerPlayer(LocalPlayer localPlayer) {
        if (localPlayer == null) {
            Insanitymod.LOGGER.warn("LocalPlayer is null in getServerPlayer");
            return null;
        }

        MinecraftServer server = localPlayer.clientLevel.getServer();
        if (server == null) {
            Insanitymod.LOGGER.warn("Server is null for LocalPlayer {}", localPlayer.getName().getString());
            return null;
        }

        ServerPlayer serverPlayer = server.getPlayerList().getPlayer(localPlayer.getUUID());
        if (serverPlayer == null) {
            Insanitymod.LOGGER.warn("ServerPlayer not found for UUID {}", localPlayer.getUUID());
        }
        return serverPlayer;
    }

    /**
     * @author Dazmonth
     * @reason adding VFX
     */
    @Overwrite
    public static void handleDodge(DodgeAnimator.DodgeDirection direction) {
        Minecraft instance = Minecraft.getInstance();
        if (instance.player != null && (instance.player.isOnGround() || ED2ClientStorage.allowAirborne()) &&
                !instance.player.isRidingJumpable() && !instance.player.isCrouching() &&
                ClientEvents.currentCooldown == 0 && instance.screen == null &&
                !instance.player.isSwimming() && instance.player.getFoodData().getFoodLevel() > 6 &&
                !instance.player.isBlocking() && FeathersHelper.spendFeathers(ED2ClientStorage.getCost())) {

            String animationDirection = DodgeAnimator.DodgeDirection.BACKWARDS.name();
            double power = ED2ClientStorage.getPower();
            double verticality = ED2ClientStorage.getVerticality();
            Vec3 look = instance.player.getLookAngle().multiply(power, 0.0F, power).normalize();
            Vec3 forwards = new Vec3(look.x, verticality, look.z);
            Vec3 backwards = new Vec3(-look.x, verticality, -look.z);
            Vec3 left = new Vec3(look.z, verticality, -look.x);
            Vec3 right = new Vec3(-look.z, verticality, look.x);
            Vec3 forwardsLeft = forwards.add(left).scale(0.5F);
            Vec3 forwardsRight = forwards.add(right).scale(0.5F);
            Vec3 backwardsLeft = backwards.add(left).scale(0.5F);
            Vec3 backwardsRight = backwards.add(right).scale(0.5F);

            switch (direction) {
                case FORWARDS:
                    instance.player.push(forwards.x, forwards.y, forwards.z);
                    animationDirection = DodgeAnimator.DodgeDirection.FORWARDS.name();
                    break;
                case BACKWARDS:
                    instance.player.push(backwards.x, backwards.y, backwards.z);
                    animationDirection = DodgeAnimator.DodgeDirection.BACKWARDS.name();
                    break;
                case LEFT:
                    instance.player.push(left.x, left.y, left.z);
                    animationDirection = DodgeAnimator.DodgeDirection.LEFT.name();
                    break;
                case RIGHT:
                    instance.player.push(right.x, right.y, right.z);
                    animationDirection = DodgeAnimator.DodgeDirection.RIGHT.name();
                    break;
                case BACKWARDS_LEFT:
                    instance.player.push(backwardsLeft.x, backwardsLeft.y, backwardsLeft.z);
                    animationDirection = DodgeAnimator.DodgeDirection.BACKWARDS_LEFT.name();
                    break;
                case BACKWARDS_RIGHT:
                    instance.player.push(backwardsRight.x, backwardsRight.y, backwardsRight.z);
                    animationDirection = DodgeAnimator.DodgeDirection.BACKWARDS_RIGHT.name();
                    break;
                case FORWARDS_LEFT:
                    instance.player.push(forwardsLeft.x, forwardsLeft.y, forwardsLeft.z);
                    animationDirection = DodgeAnimator.DodgeDirection.FORWARDS_LEFT.name();
                    break;
                case FORWARDS_RIGHT:
                    instance.player.push(forwardsRight.x, forwardsRight.y, forwardsRight.z);
                    animationDirection = DodgeAnimator.DodgeDirection.FORWARDS_RIGHT.name();
                    break;
            }

            LocalPlayer localPlayer = instance.player;
            ServerPlayer serverPlayer = getServerPlayer(localPlayer);
            Vec3 pos = localPlayer.position();
            if (serverPlayer != null) {
                spawnParticles(serverPlayer.getLevel(), pos, 20); // Используем ServerLevel, если доступен
            } else {
                spawnParticles(localPlayer.level, pos, 10); // Иначе используем клиентский уровень
            }

            ClientEvents.currentCooldown = ED2ClientStorage.getCooldown();
            ED2Messages.sendToServer(new DodgeEffectsCTSPacket(animationDirection));
        }
    }
}