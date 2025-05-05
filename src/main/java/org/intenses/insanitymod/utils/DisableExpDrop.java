package org.intenses.insanitymod.utils;

import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;


@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID)
public class DisableExpDrop {


    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getEntity().getName().toString()=="twilightforest:yeti"){
                event.setDroppedExperience(0);
        }
    }
}