package org.intenses.insanitymod.Items;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.network.ItemModePacket;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Optional;

import static org.intenses.insanitymod.Insanitymod.*;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
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
                boolean inCuriosSlot = false;

                try {
                    inCuriosSlot = CuriosApi.getCuriosHelper()
                            .findFirstCurio(player, SPECIAL_ITEM.get())
                            .isPresent();
                } catch (NoClassDefFoundError e) {
                    LOGGER.debug("Curios API not found, skipping Curious slot check.");
                }

                if (ACTIVATE_KEY.consumeClick()) {
                    if (mainHand.getItem() instanceof SpecialItem) {
                        boolean newActive = !SpecialItem.isActive(mainHand);
                        SpecialItem.setActive(mainHand, newActive);
                        int slot = player.getInventory().selected; // 0–8 для горячей панели
                        Insanitymod.LOGGER.info("Sending ItemModePacket for slot: {}, isActive: {}, mode: {}", slot, newActive, SpecialItem.getMode(mainHand));
                        Insanitymod.NETWORK.sendToServer(new ItemModePacket(slot, newActive, SpecialItem.getMode(mainHand)));
                    } else if (offHand.getItem() instanceof SpecialItem) {
                        boolean newActive = !SpecialItem.isActive(offHand);
                        SpecialItem.setActive(offHand, newActive);
                        int slot = 40; // Вторая рука
                        Insanitymod.LOGGER.info("Sending ItemModePacket for slot: {}, isActive: {}, mode: {}", slot, newActive, SpecialItem.getMode(offHand));
                        Insanitymod.NETWORK.sendToServer(new ItemModePacket(slot, newActive, SpecialItem.getMode(offHand)));
                    } else if (inCuriosSlot) {
                        Optional<SlotResult> slotResultOpt = CuriosApi.getCuriosHelper()
                                .findFirstCurio(player, SPECIAL_ITEM.get());
                        if (slotResultOpt.isPresent()) {
                            SlotResult slotResult = slotResultOpt.get();
                            ItemStack stack = slotResult.stack();
                            boolean newActive = !SpecialItem.isActive(stack);
                            SpecialItem.setActive(stack, newActive);

                            SlotContext slotContext = slotResult.slotContext();
                            CuriosApi.getCuriosHelper().setEquippedCurio(player, slotContext.getIdentifier(), slotContext.getIndex(), stack);
                        }
                    }
                }

                if (SWITCH_MODE_KEY.consumeClick()) {
                    if (mainHand.getItem() instanceof SpecialItem) {
                        int currentMode = SpecialItem.getMode(mainHand);
                        int newMode = (currentMode + 1) % 3;
                        SpecialItem.setMode(mainHand, newMode);
                        int slot = player.getInventory().selected; // 0–8 для горячей панели
                        Insanitymod.LOGGER.info("Sending ItemModePacket for slot: {}, isActive: {}, mode: {}", slot, SpecialItem.isActive(mainHand), newMode);
                        Insanitymod.NETWORK.sendToServer(new ItemModePacket(slot, SpecialItem.isActive(mainHand), newMode));
                    } else if (offHand.getItem() instanceof SpecialItem) {
                        int currentMode = SpecialItem.getMode(offHand);
                        int newMode = (currentMode + 1) % 3;
                        SpecialItem.setMode(offHand, newMode);
                        int slot = 40; // Вторая рука
                        Insanitymod.LOGGER.info("Sending ItemModePacket for slot: {}, isActive: {}, mode: {}", slot, SpecialItem.isActive(offHand), newMode);
                        Insanitymod.NETWORK.sendToServer(new ItemModePacket(slot, SpecialItem.isActive(offHand), newMode));
                    } else if (inCuriosSlot) {
                        Optional<SlotResult> slotResultOpt = CuriosApi.getCuriosHelper()
                                .findFirstCurio(player, SPECIAL_ITEM.get());
                        if (slotResultOpt.isPresent()) {
                            SlotResult slotResult = slotResultOpt.get();
                            ItemStack stack = slotResult.stack();
                            int currentMode = SpecialItem.getMode(stack);
                            int newMode = (currentMode + 1) % 3;
                            SpecialItem.setMode(stack, newMode);

                            SlotContext slotContext = slotResult.slotContext();
                            CuriosApi.getCuriosHelper().setEquippedCurio(player, slotContext.getIdentifier(), slotContext.getIndex(), stack);
                        }
                    }
                }
            }
        }
    }
}