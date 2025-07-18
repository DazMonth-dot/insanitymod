package org.intenses.insanitymod.utils;


import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, value = Dist.CLIENT)
public class ToolTipShit {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getEntity() == null) return;
        List<Component> tooltip = event.getToolTip();

        for (int i = 0; i < tooltip.size(); i++) {
            Component component = tooltip.get(i);
            ComponentContents contents = component.getContents();
            if (contents instanceof TranslatableContents translatable) {
                if (translatable.getKey().equals("item.modifiers.mainhand")) {
                    tooltip.subList(i, tooltip.size()).clear();
                    break;
                }
            }
        }
        while (!tooltip.isEmpty() && tooltip.get(tooltip.size() - 1).getString().isBlank()) {
            tooltip.remove(tooltip.size() - 1);
        }
        tooltip.add(Component.empty());
    }
}
