package org.intenses.insanitymod.Items;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.network.ItemModePacket;
import top.theillusivec4.curios.api.CuriosApi;

import static org.intenses.insanitymod.Insanitymod.*;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KeyHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getInstance().level == null) {
            return;
        } else {
            if (event.phase == TickEvent.Phase.END) {
                Player player = Minecraft.getInstance().player;
                if (player == null) return;

                ItemStack mainHand = player.getMainHandItem();
                ItemStack offHand = player.getOffhandItem();
                boolean inCuriousSlot = false;

                try {
                    inCuriousSlot = CuriosApi.getCuriosHelper()
                            .findFirstCurio(player, SPECIAL_ITEM.get())
                            .isPresent();
                } catch (NoClassDefFoundError e) {
                    LOGGER.debug("Curios API not found, skipping Curious slot check.");
                }

                if (ACTIVATE_KEY.isDown() && (mainHand.getItem() instanceof SpecialItem
                        || offHand.getItem() instanceof SpecialItem
                        || inCuriousSlot)) {
                    ItemStack stack = mainHand.getItem() instanceof SpecialItem ? mainHand : offHand;
                    boolean newActive = !SpecialItem.isActive(stack);
                    SpecialItem.setActive(stack, newActive);

                    // Отправка на сервер
                    int slot = player.getInventory().selected + 36; // Главная рука
                    if (offHand.getItem() instanceof SpecialItem) slot = 45; // Вторая рука
                    Insanitymod.NETWORK.sendToServer(new ItemModePacket(slot, newActive, SpecialItem.getMode(stack)));
                }

                if (SWITCH_MODE_KEY.isDown() && (mainHand.getItem() instanceof SpecialItem
                        || offHand.getItem() instanceof SpecialItem
                        || inCuriousSlot)) {
                    ItemStack stack = mainHand.getItem() instanceof SpecialItem ? mainHand : offHand;
                    int currentMode = SpecialItem.getMode(stack);
                    int newMode = (currentMode + 1) % 3;
                    SpecialItem.setMode(stack, newMode);

                    // Отправка на сервер
                    int slot = player.getInventory().selected + 36; // Главная рука
                    if (offHand.getItem() instanceof SpecialItem) slot = 45; // Вторая рука
                    Insanitymod.NETWORK.sendToServer(new ItemModePacket(slot, SpecialItem.isActive(stack), newMode));
                }
            }
        }
    }
}

