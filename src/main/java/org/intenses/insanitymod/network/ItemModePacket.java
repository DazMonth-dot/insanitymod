package org.intenses.insanitymod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import org.intenses.insanitymod.Items.SpecialItem;


import java.util.function.Supplier;

public class ItemModePacket {
    private final int slot; // Слот инвентаря (например, 0 для главной руки, 1 для второй, или слот Curios)
    private final boolean isActive;
    private final int mode;

    public ItemModePacket(int slot, boolean isActive, int mode) {
        this.slot = slot;
        this.isActive = isActive;
        this.mode = mode;
    }

    // Метод для отправки данных в буфер
    public static void encode(ItemModePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.slot);
        buf.writeBoolean(msg.isActive);
        buf.writeInt(msg.mode);
    }

    // Метод для чтения данных из буфера
    public static ItemModePacket decode(FriendlyByteBuf buf) {
        return new ItemModePacket(buf.readInt(), buf.readBoolean(), buf.readInt());
    }

    // Обработчик пакета на стороне сервера
    public static void handle(ItemModePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                // Проверяем слоты в инвентаре игрока
                ItemStack stack = player.getInventory().getItem(msg.slot);
                if (stack.getItem() instanceof SpecialItem) {
                    SpecialItem.setActive(stack, msg.isActive);
                    SpecialItem.setMode(stack, msg.mode);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
