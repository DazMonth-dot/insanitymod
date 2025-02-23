package org.intenses.insanitymod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.Items.SpecialItem;

import java.util.function.Supplier;

/**
 * Пакет для передачи данных о состоянии и режиме предмета SpecialItem между клиентом и сервером.
 */
public class ItemModePacket {
    /** Индекс слота в инвентаре игрока (0-40) */
    private final int slot;
    /** Состояние активации предмета (true - активен, false - неактивен) */
    private final boolean isActive;
    /** Режим предмета (целое число, представляющее режим) */
    private final int mode;

    /**
     * Конструктор пакета.
     *
     * @param slot     Индекс слота в инвентаре (0-8 для горячей панели, 9-35 для основного инвентаря, 36-39 для брони, 40 для второй руки)
     * @param isActive Новое состояние активации предмета
     * @param mode     Новый режим предмета
     */
    public ItemModePacket(int slot, boolean isActive, int mode) {
        this.slot = slot;
        this.isActive = isActive;
        this.mode = mode;
    }

    /**
     * Кодирует данные пакета в буфер для отправки по сети.
     *
     * @param packet Пакет для кодирования
     * @param buf    Буфер для записи данных
     */
    public static void encode(ItemModePacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.slot);
        buf.writeBoolean(packet.isActive);
        buf.writeInt(packet.mode);
    }

    /**
     * Декодирует данные из буфера для создания объекта пакета.
     *
     * @param buf Буфер с данными
     * @return Новый экземпляр ItemModePacket
     */
    public static ItemModePacket decode(FriendlyByteBuf buf) {
        int slot = buf.readInt();
        boolean isActive = buf.readBoolean();
        int mode = buf.readInt();
        return new ItemModePacket(slot, isActive, mode);
    }

    /**
     * Обрабатывает пакет на стороне сервера, обновляя состояние предмета в инвентаре игрока.
     *
     * @param packet Пакет для обработки
     * @param ctx    Контекст сетевого события
     */
    public static void handle(ItemModePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Получаем игрока, отправившего пакет
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.getInventory() != null) {
                // Проверяем, что индекс слота находится в допустимом диапазоне (0-40)
                if (packet.slot >= 0 && packet.slot < 41) {
                    ItemStack stack = player.getInventory().getItem(packet.slot);
                    // Проверяем, является ли предмет в слоте экземпляром SpecialItem
                    if (stack.getItem() instanceof SpecialItem specialItem) {
                        // Обновляем состояние и режим предмета
                        specialItem.setActive(stack, packet.isActive);
                        specialItem.setMode(stack, packet.mode);
                    } else {
                        Insanitymod.LOGGER.warn("Предмет в слоте {} не является SpecialItem", packet.slot);
                    }
                } else {
                    Insanitymod.LOGGER.warn("Получен ItemModePacket с некорректным слотом: {}", packet.slot);
                }
            } else {
                Insanitymod.LOGGER.error("Не удалось обработать ItemModePacket: игрок или инвентарь null");
            }
        });
        // Помечаем пакет как обработанный
        ctx.get().setPacketHandled(true);
    }
}