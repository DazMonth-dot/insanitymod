package org.intenses.insanitymod.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.intenses.insanitymod.Insanitymod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ProtTweak {
    private static final ResourceLocation VEILED_EFFECT_ID = new ResourceLocation("engulfingdarkness", "veiled");
    private static final TagKey<Item> DARK_PROT_TAG = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(Insanitymod.MOD_ID, "dark_prot"));
    private static final TagKey<Item> DARK_PROT_WATER_TAG = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(Insanitymod.MOD_ID, "dark_prot_water"));

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event.player.level, event.player);
        }
    }

    private static void execute(LevelAccessor world, Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return;

        MobEffect veiledEffect = ForgeRegistries.MOB_EFFECTS.getValue(VEILED_EFFECT_ID);
        if (veiledEffect == null) {
            Insanitymod.LOGGER.warn("Veiled effect from Engulfing Darkness not found!");
            return;
        }

        boolean isInWater = livingEntity.isEyeInFluidType(Fluids.WATER.getFluidType());
        boolean hasWaterSafeItem = hasTaggedItem(livingEntity, DARK_PROT_WATER_TAG);
        boolean hasSafeItem = hasTaggedItem(livingEntity, DARK_PROT_TAG);

        boolean shouldApplyEffect = (hasSafeItem && !isInWater) || hasWaterSafeItem;

        if (!world.isClientSide()) {
            MobEffectInstance existingEffect = livingEntity.getEffect(veiledEffect);
            if (shouldApplyEffect) {
                if (existingEffect == null) {
                    livingEntity.addEffect(new MobEffectInstance(veiledEffect, Integer.MAX_VALUE, 0, true, false));
                }
            } else {
                if (existingEffect != null) {
                    livingEntity.removeEffect(veiledEffect);
                }
            }
        }
    }

    private static boolean hasTaggedItem(LivingEntity entity, TagKey<Item> tag) {
        return entity.getMainHandItem().is(tag) ||
                entity.getOffhandItem().is(tag) ||
                entity.getItemBySlot(EquipmentSlot.HEAD).is(tag) ||
                hasCurioWithTags(entity, tag);
    }

    private static boolean hasCurioWithTags(LivingEntity entity, TagKey<Item> tag) {
        return !CuriosApi.getCuriosHelper().findCurios(entity, stack -> stack.is(tag)).isEmpty();
    }
}