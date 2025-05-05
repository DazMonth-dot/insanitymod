package org.intenses.insanitymod.panic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int packetId = 2;

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("insanitymod", "panic"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        INSTANCE.registerMessage(packetId++,
                org.intenses.insanitymod.panic.PanicSyncPacket.class,
                org.intenses.insanitymod.panic.PanicSyncPacket::encode,
                org.intenses.insanitymod.panic.PanicSyncPacket::decode,
                org.intenses.insanitymod.panic.PanicSyncPacket::handle
        );
    }

    public static void sendToClient(ServerPlayer player, PanicSyncPacket packet) {
        INSTANCE.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }


}