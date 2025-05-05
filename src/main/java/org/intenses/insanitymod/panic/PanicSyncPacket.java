package org.intenses.insanitymod.panic;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.intenses.insanitymod.panic.PanicAttributes;

import java.util.function.Supplier;

public record PanicSyncPacket(double panic) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(panic);
    }

    public static PanicSyncPacket decode(FriendlyByteBuf buf) {
        return new PanicSyncPacket(buf.readDouble());
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            PanicAttributes.clientPanic = (float) this.panic;
        });
        ctx.get().setPacketHandled(true);
    }
}