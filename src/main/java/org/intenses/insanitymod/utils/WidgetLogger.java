package org.intenses.insanitymod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WidgetLogger {

    @SubscribeEvent @OnlyIn(Dist.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (Minecraft.getInstance().options.autoJump().get()) {
                Minecraft.getInstance().options.autoJump().set(false);
            }
        }
    }
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        Screen screen = event.getScreen();

        if (screen instanceof ControlsScreen controlsScreen) {
            List<AbstractWidget> toRemove = new ArrayList<>();
            int index = 0;
            for (var child : controlsScreen.children()) {
                if (child instanceof AbstractWidget widget) {
                    if (index >= 4 && index <= 6) {
                        toRemove.add(widget);
                    }
                }
                index++;
            }


            for (AbstractWidget widget : toRemove) {
                event.removeListener(widget);
                controlsScreen.children().remove(widget);
            }


        }
    }
}