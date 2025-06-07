package org.intenses.insanitymod.utils;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class AnvilTweak {

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (!right.isEmpty() && left.isDamaged()) {
            Item repairMaterial = left.getItem().getCraftingRemainingItem();
            if (repairMaterial == right.getItem() || right.getItem() == Items.DIAMOND) {
                ItemStack output = left.copy();
                int repairAmount = (int)(output.getMaxDamage() * 0.35);
                output.setDamageValue(Math.max(output.getDamageValue() - repairAmount, 0));
                event.setOutput(output);
                event.setCost(0);
                event.setMaterialCost(1);
            }
        }
    }
}
