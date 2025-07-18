package org.intenses.insanitymod.mixins;

import croissantnova.sanitydim.capability.ISanity;
import croissantnova.sanitydim.config.ConfigProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import org.intenses.insanitymod.utils.SittingPlayersHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;

@Mixin(value = croissantnova.sanitydim.passive.Darkness.class, remap = false)
public class LightSanityMixin {

    @Unique
    private static final ResourceLocation VEILED_EFFECT_ID =
            new ResourceLocation("engulfingdarkness", "veiled");

    @Unique
    private boolean insanitymod$hasVeiledEffect(ServerPlayer player) {
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(VEILED_EFFECT_ID);
        return effect != null && player.hasEffect(effect);
    }

    @Unique
    private boolean insanitymod$isSitting(ServerPlayer player) {
        return SittingPlayersHelper.isPlayerSitting(player.getUUID()) || player.getVehicle() != null;
    }


    /**
     * @author DazMonth
     * @reason hz
     */
    @Overwrite
    public float get(@Nonnull ServerPlayer player, @Nonnull ISanity cap, @Nonnull ResourceLocation dim) {
        BlockPos pos = player.blockPosition();

        if (insanitymod$hasVeiledEffect(player)) {
            return 0.0F;
        }

        if (insanitymod$isSitting(player)) {
            pos = pos.above();
        }

        int lightLevel = player.level.getMaxLocalRawBrightness(pos);
        int threshold = ConfigProxy.getDarknessThreshold(dim);

        return lightLevel <= threshold ? ConfigProxy.getDarkness(dim) : 0.0F;
    }
}