package org.intenses.insanitymod.classChoose;


import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class firstJoin {
    private static boolean firstJoinChecked = false;

    @SubscribeEvent
    public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        Minecraft mc = Minecraft.getInstance();
        System.out.println("[DEBUG] fIRST JOIN");
        if (!firstJoinChecked) {
            processFirstJoinClient(mc);
            firstJoinChecked = true;
        }
    }

    public static void processFirstJoinClient(Minecraft mc) {
        assert mc.player != null;
        CompoundTag persistentRoot = mc.player.getPersistentData();
        CompoundTag modData = persistentRoot.getCompound(Insanitymod.MOD_ID);

        if (!modData.getBoolean("first_join")) {
            modData.putBoolean("first_join", true);
            persistentRoot.put(Insanitymod.MOD_ID, modData);
        }
    }
}