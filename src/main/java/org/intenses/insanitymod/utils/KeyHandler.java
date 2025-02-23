package org.intenses.insanitymod.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.Items.SpecialItem;
import org.intenses.insanitymod.network.ItemModePacket;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class KeyHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) return;

        Player player = Minecraft.getInstance().player;
        ItemStack stack = findSpecialItem(player);

        if (stack.isEmpty()) {
            return; // Если предмета нет в "necklace" — выходим
        }

        boolean isActive = SpecialItem.isActive(stack);
        int mode = SpecialItem.getMode(stack);

        // Логика переключения состояния
        if (Insanitymod.ACTIVATE_KEY.consumeClick()) {
            boolean newActive = !isActive;
            SpecialItem.setActive(stack, newActive);
            if (!newActive && stack.getItem() instanceof SpecialItem specialItem) {
                specialItem.removeEffects(player, mode); // Удаляем эффект текущего режима при деактивации через экземпляр
            }
            sendPacket(player, newActive, mode, mode); // Передаём предыдущий и новый режим (одинаковые при активации/деактивации)
        } else if (Insanitymod.SWITCH_MODE_KEY.consumeClick()) {
            int previousMode = mode;
            int newMode = (mode + 1) % 3; // Циклическое переключение 0 → 1 → 2 → 0
            if (!isActive) {
                SpecialItem.setMode(stack, newMode); // Обновляем режим, но не применяем эффекты
                sendPacket(player, isActive, previousMode, newMode); // Отправляем обновление режима на сервер
            } else {
                if (stack.getItem() instanceof SpecialItem specialItem) {
                    specialItem.removeEffects(player, previousMode); // Удаляем эффект предыдущего режима через экземпляр
                    specialItem.applyEffects(player, previousMode, newMode); // Применяем новый эффект через экземпляр
                }
                SpecialItem.setMode(stack, newMode);
                sendPacket(player, isActive, previousMode, newMode); // Передаём предыдущий и новый режим
            }
        }
    }

    private static ItemStack findSpecialItem(Player player) {
        ItemStack stack = CuriosApi.getCuriosHelper()
                .findFirstCurio(player, Insanitymod.SPECIAL_ITEM.get())
                .filter(result -> "necklace".equals(result.slotContext().identifier()))
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
        return stack;
    }

    private static void sendPacket(Player player, boolean isActive, int previousMode, int newMode) {
        Insanitymod.NETWORK.sendToServer(new ItemModePacket(isActive, previousMode, newMode));
    }
}