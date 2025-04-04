package org.intenses.insanitymod.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;


@Mod.EventBusSubscriber(modid = "insanitymod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AmnesiaScrollHandler {

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack itemStack = event.getItemStack();
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
        if (itemId != null && itemId.equals(new ResourceLocation("skilltree", "amnesia_scroll"))) {
            if (!player.level.isClientSide) {
                applyEffectsToPlayer(player);
            }
        }
    }

    private static void applyEffectsToPlayer(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 600, 0));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 1));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 600, 1));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 0));
        player.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));

    }
}