package org.intenses.insanitymod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.intenses.insanitymod.Items.SpecialItem;
import org.intenses.insanitymod.network.ItemModePacket;
import org.intenses.insanitymod.utils.SoulLampCheck;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.UUID;

import org.intenses.insanitymod.utils.featherAttribute;

@Mod(Insanitymod.MOD_ID)
public class Insanitymod {
    public static final String MOD_ID = "insanitymod";
    public static final Logger LOGGER = LogUtils.getLogger();

    //Registries
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> SPECIAL_ITEM = ITEMS.register("amulet",
            () -> new SpecialItem(new Item.Properties().stacksTo(1)));

    //Bindings
    public static final KeyMapping ACTIVATE_KEY = new KeyMapping("key.insanitymod.activate", GLFW.GLFW_KEY_L, "category.insanitymod");
    public static final KeyMapping SWITCH_MODE_KEY = new KeyMapping("key.insanitymod.switch_mode", GLFW.GLFW_KEY_H, "category.insanitymod");

    //Channel
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public Insanitymod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        NETWORK.registerMessage(0, ItemModePacket.class, ItemModePacket::encode, ItemModePacket::decode, ItemModePacket::handle);
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        featherAttribute.processFirstJoin(event.getEntity());
        featherAttribute.setPlayerAttributes(event.getEntity());

    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        featherAttribute.setPlayerAttributes(event.getEntity());
        event.getEntity().addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        SoulLampCheck.ApplyEffect(event);
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().getWorldPath(LevelResource.ROOT).toFile();
        }
    }



    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ItemProperties.register(SPECIAL_ITEM.get(), new ResourceLocation("insanitymod", "is_active"),
                        (stack, level, entity, seed) -> SpecialItem.isActive(stack) ? 1.0F : 0.0F);
                ItemProperties.register(SPECIAL_ITEM.get(), new ResourceLocation("insanitymod", "mode"),
                        (stack, level, entity, seed) -> (float) SpecialItem.getMode(stack));
            });
        }
    }
}