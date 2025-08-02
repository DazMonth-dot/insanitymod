package org.intenses.insanitymod.classChoose;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class SetOriginPacket {
    private final ResourceLocation originId;

    public SetOriginPacket(ResourceLocation originId) {
        this.originId = originId;
    }

    public static void encode(SetOriginPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.originId);
    }

    public static SetOriginPacket decode(FriendlyByteBuf buf) {
        return new SetOriginPacket(buf.readResourceLocation());
    }

    public static void handle(SetOriginPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                OriginsStuff.setPlayerOrigin(player, msg.originId);
            }
        });

        ctx.get().setPacketHandled(true);
    }
}