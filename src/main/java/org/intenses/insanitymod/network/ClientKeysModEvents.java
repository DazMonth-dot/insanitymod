package org.intenses.insanitymod.network;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = net.minecraftforge.api.distmarker.Dist.CLIENT)
public class ClientKeysModEvents {

    // Bindings
    public static final KeyMapping ACTIVATE_KEY = new KeyMapping("key.insanitymod.activate", GLFW.GLFW_KEY_L, "category.insanitymod");
    public static final KeyMapping SWITCH_MODE_KEY = new KeyMapping("key.insanitymod.switch_mode", GLFW.GLFW_KEY_H, "category.insanitymod");

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ACTIVATE_KEY);
        event.register(SWITCH_MODE_KEY);
    }
}
