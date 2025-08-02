    package org.intenses.insanitymod.QoL;

    import net.minecraft.nbt.CompoundTag;
    import net.minecraft.resources.ResourceLocation;
    import net.minecraft.server.level.ServerPlayer;
    import net.minecraft.world.effect.MobEffectInstance;
    import net.minecraft.world.effect.MobEffects;
    import net.minecraft.world.entity.ai.attributes.Attribute;
    import net.minecraft.world.entity.ai.attributes.AttributeInstance;
    import net.minecraft.world.entity.ai.attributes.AttributeModifier;
    import net.minecraft.world.entity.player.Player;
    import net.minecraftforge.event.entity.player.PlayerEvent;
    import net.minecraftforge.eventbus.api.SubscribeEvent;
    import net.minecraftforge.fml.common.Mod;
    import net.minecraftforge.network.PacketDistributor;
    import net.minecraftforge.registries.ForgeRegistries;
    import org.intenses.insanitymod.Insanitymod;
    import org.intenses.insanitymod.classChoose.firstJoinPacket;

    import java.util.UUID;


    @Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public class ModAttributes {

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            Player player = event.getEntity();
            ModAttributes.processFirstJoin(player);
            ModAttributes.setPlayerAttributes(player);
        }

        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            ModAttributes.setPlayerAttributes(event.getEntity());
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));
        }

        public static void processFirstJoin(Player player) {
            CompoundTag persistentRoot = player.getPersistentData();
            CompoundTag modData = persistentRoot.getCompound(Insanitymod.MOD_ID);
            if (!modData.getBoolean("first_join")) {
                setPlayerAttributes(player);
                player.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));
                sendFirstJoinGui((ServerPlayer) player);
                modData.putBoolean("first_join", true);
                persistentRoot.put(Insanitymod.MOD_ID, modData);
            }
        }

        public static void setPlayerAttributes(Player player) {
            if (player != null && !player.level.isClientSide()) {
                applyModifier(player, net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH,
                        UUID.fromString("5D6F0BA2-1186-46AC-B896-C61C5CEE99CC"), "insanity_max_health_mod", -10);

                Attribute featherAttr = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation("feathers", "feathers.max_feathers"));
                    applyModifier(player, featherAttr,
                            UUID.fromString("6E7F1CB3-2A92-4F1A-8D39-1123AB5678CD"), "insanity_max_feathers_mod", -16);




                player.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));
            }
        }


        private static void applyModifier(Player player, Attribute attribute, UUID modifierUUID, String modifierName, float amount) {
            AttributeInstance attrInstance = player.getAttribute(attribute);
            if (attrInstance != null) {
                attrInstance.removeModifier(modifierUUID);
                attrInstance.addPermanentModifier(new AttributeModifier(modifierUUID, modifierName, amount, AttributeModifier.Operation.ADDITION));
            }
        }

        public static void sendFirstJoinGui(ServerPlayer player) {
            Insanitymod.NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new firstJoinPacket());
        }
    }