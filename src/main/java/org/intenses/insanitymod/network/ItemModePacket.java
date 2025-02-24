package org.intenses.insanitymod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.Items.SpecialItem;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.function.Supplier;

public class ItemModePacket {
    private final boolean isActive;
    private final int previousMode;
    private final int newMode;

    public ItemModePacket(boolean isActive, int previousMode, int newMode) {
        this.isActive = isActive;
        this.previousMode = previousMode;
        this.newMode = newMode;
    }

    public static void encode(ItemModePacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.isActive);
        buf.writeInt(packet.previousMode);
        buf.writeInt(packet.newMode);
    }

    public static ItemModePacket decode(FriendlyByteBuf buf) {
        return new ItemModePacket(buf.readBoolean(), buf.readInt(), buf.readInt());
    }

    public static void handle(ItemModePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }

            ItemStack stack = CuriosApi.getCuriosHelper()
                    .findFirstCurio(player, Insanitymod.SPECIAL_ITEM.get())
                    .filter(result -> "necklace".equals(result.slotContext().identifier()))
                    .map(SlotResult::stack)
                    .orElse(ItemStack.EMPTY);

            if (stack.isEmpty()) {
                return;
            } else if (stack.getItem() instanceof SpecialItem specialItem) {
                specialItem.setActive(stack, packet.isActive);
                int normalizedNewMode = specialItem.normalizeMode(packet.newMode); // Нормализуем новый режим
                specialItem.setMode(stack, normalizedNewMode);
                if (packet.isActive) {
                    int normalizedPreviousMode = specialItem.normalizeMode(packet.previousMode); // Нормализуем предыдущий режим
                    specialItem.applyEffects(player, normalizedPreviousMode, normalizedNewMode); // Удаляем эффект предыдущего режима и применяем новый
                } else {
                    specialItem.removeEffects(player, normalizedNewMode); // Удаляем эффект нормализованного текущего режима при деактивации
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}