package org.intenses.insanitymod.mixins;

import dev.quarris.engulfingdarkness.EngulfingDarkness;
import dev.quarris.engulfingdarkness.ModConfigs;
import dev.quarris.engulfingdarkness.capability.Darkness;
import dev.quarris.engulfingdarkness.packets.EnteredDarknessMessage;
import dev.quarris.engulfingdarkness.packets.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Darkness.class,remap = false)
public abstract class DarknessMixin {
    @Shadow public abstract boolean isResistant(Player player);

    @Shadow public abstract void setInDarkness(boolean inDarkness);

    @Shadow private boolean isInDarkness;

    @Shadow private float darknessLevel;

    @Shadow private float dangerLevel;

    /**
     * @author DazMonth
     * @reason adding spec check up
     */
    @Overwrite
    public void tick(Player player) {
        if (!player.level.isClientSide() && ModConfigs.isAllowed(player.level.dimension().location())) {
            if (this.isResistant(player)) {
                this.setInDarkness(false);
                PacketHandler.sendToClient(new EnteredDarknessMessage(false), player);
            } else {
                int light = player.level.getMaxLocalRawBrightness(new BlockPos(player.getEyePosition(1.0F)));
                if (!this.isInDarkness || !this.isResistant(player) && light <= (Integer)ModConfigs.darknessLightLevel.get()) {
                    if (!player.isSpectator() &&!player.isCreative() && !this.isInDarkness && light <= (Integer)ModConfigs.darknessLightLevel.get()) {
                        this.setInDarkness(true);
                        PacketHandler.sendToClient(new EnteredDarknessMessage(true), player);
                    }
                } else {
                    this.setInDarkness(false);
                    PacketHandler.sendToClient(new EnteredDarknessMessage(false), player);
                }
            }
        }

        if (this.isInDarkness) {
            this.darknessLevel = (float)Math.min((double)this.darknessLevel + (Double)ModConfigs.darknessLevelIncrement.get(), (double)1.0F);
            if ((double)this.darknessLevel == (double)1.0F) {
                this.dangerLevel = (float)Math.min((double)this.dangerLevel + (Double)ModConfigs.dangerLevelIncrement.get(), (double)1.0F);
            }
        } else {
            this.darknessLevel = (float)Math.max((double)this.darknessLevel - (double)3.0F * (Double)ModConfigs.darknessLevelIncrement.get(), (double)0.0F);
            this.dangerLevel = 0.0F;
        }

        if (!player.level.isClientSide() && this.isInDarkness) {
            double rx = ((double)-1.0F + Math.random() * (double)2.0F) * 0.3;
            double ry = Math.random();
            double rz = ((double)-1.0F + Math.random() * (double)2.0F) * 0.3;
            double mx = player.getRandom().nextGaussian() * 0.2;
            double my = player.getRandom().nextGaussian() * 0.2;
            double mz = player.getRandom().nextGaussian() * 0.2;
            ((ServerLevel)player.level).sendParticles(ParticleTypes.SMOKE, player.getX() + rx, player.getY() + ry, player.getZ() + rz, (int)(this.darknessLevel * 10.0F), mx, my, mz, 0.01);
        }

        if (player.level.isClientSide() && (double)this.darknessLevel == (double)1.0F && player.getRandom().nextDouble() > 0.95) {
            player.playSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 1.0F, 0.1F);
        }

        if ((double)this.dangerLevel == (double)1.0F && (Double)ModConfigs.darknessDamage.get() != (double)0.0F) {
            player.hurt(EngulfingDarkness.damageSource, ((Double)ModConfigs.darknessDamage.get()).floatValue());
        }

    }
}
