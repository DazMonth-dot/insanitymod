package org.intenses.insanitymod.classChoose;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class firstJoinPacket {

    public firstJoinPacket() {}

    public static void encode(firstJoinPacket msg, FriendlyByteBuf buf) {
    }

    public static firstJoinPacket decode(FriendlyByteBuf buf) {
        return new firstJoinPacket();
    }

    public static void handle(firstJoinPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () ->
                    GUI::openGui);
        });

        ctx.get().setPacketHandled(true);
    }
}