package org.intenses.insanitymod.QoL;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class AnvilTweak {

    private static final TagKey<Item> REPAIR_MATERIALS_TAG = ItemTags.create(new ResourceLocation(Insanitymod.MOD_ID, "repair_materials"));

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (!right.isEmpty() && left.isDamaged()) {
            if (right.is(REPAIR_MATERIALS_TAG)) {
                ItemStack output = left.copy();
                int repairAmount = (int)(output.getMaxDamage() * 0.35);
                int newDamage = output.getDamageValue() - repairAmount;
                output.setDamageValue(Math.max(newDamage, 0));
                event.setOutput(output);
                event.setCost(0);
                event.setMaterialCost(1);
            }
        }
    }
}

