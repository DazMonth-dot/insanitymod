package org.intenses.insanitymod.utils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.panic.StressBarRenderer;


@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientUtils {
//    @SubscribeEvent
//    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
//        event.registerAboveAll("stress_bar", new StressBarRenderer());
//    }
}