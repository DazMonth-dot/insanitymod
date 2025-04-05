package org.intenses.insanitymod.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.intenses.insanitymod.Insanitymod;

import java.util.UUID;

public class ModAttributes {

    public static void processFirstJoin(Player player) {
        CompoundTag persistentRoot = player.getPersistentData();
        CompoundTag modData = persistentRoot.getCompound(Insanitymod.MOD_ID);
        if (!modData.getBoolean("first_join")) {
            setPlayerAttributes(player);
            player.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));
            modData.putBoolean("first_join", true);
            persistentRoot.put(Insanitymod.MOD_ID, modData);
        }
    }

    public static void setPlayerAttributes(Player player) {
        if (player != null && !player.level.isClientSide()) {
            applyModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH,
                    UUID.fromString("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC"), "insanity_max_health_mod", -14);

            Attribute featherAttr = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("feathers", "max_feathers"));
            if (featherAttr != null && player.getAttribute(featherAttr) != null) {
                applyModifier(player, featherAttr,
                        UUID.fromString("6E7F1CB3-2A92-4F1A-8D39-1123AB5678CD"), "insanity_max_feathers_mod", -16);
            }

            player.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));
        }
    }

    private static void applyModifier(Player player, Attribute attribute, UUID modifierUUID, String modifierName, float amount) {
        AttributeInstance attrInstance = player.getAttribute(attribute);
        if (attrInstance != null) {
            attrInstance.removeModifier(modifierUUID);
            attrInstance.addPermanentModifier(new AttributeModifier(modifierUUID, modifierName, amount, AttributeModifier.Operation.ADDITION));
        }
    }
}