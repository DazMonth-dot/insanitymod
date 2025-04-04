package org.intenses.insanitymod;

import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.intenses.insanitymod.Items.ShieldTweaks;
import org.intenses.insanitymod.Items.SpecialItem;
import org.intenses.insanitymod.music.ModSounds;
import org.intenses.insanitymod.network.ItemModePacket;

import org.intenses.insanitymod.utils.featherAttribute;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(Insanitymod.MOD_ID)
public class Insanitymod {
    public static final String MOD_ID = "insanitymod";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Registries
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<Item> SPECIAL_ITEM = ITEMS.register("amulet",
            () -> new SpecialItem(new Item.Properties().stacksTo(1)));

    // Bindings
    public static final KeyMapping ACTIVATE_KEY = new KeyMapping("key.insanitymod.activate", GLFW.GLFW_KEY_L, "category.insanitymod");
    public static final KeyMapping SWITCH_MODE_KEY = new KeyMapping("key.insanitymod.switch_mode", GLFW.GLFW_KEY_H, "category.insanitymod");

    // Channel
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();
    private static int messageID = 0;

    public Insanitymod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modEventBus);
        ModSounds.register(modEventBus);
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ShieldTweaks());


        NETWORK.registerMessage(0, ItemModePacket.class, ItemModePacket::encode, ItemModePacket::decode, ItemModePacket::handle);
    }

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        NETWORK.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        ++messageID;
    }

    // Очередь задач для выполнения
    public static void queueServerWork(int tick, Runnable action) {
        workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
            workQueue.forEach((work) -> {
                work.setValue(work.getValue() - 1);
                if (work.getValue() == 0) {
                    actions.add(work);
                }
            });
            actions.forEach((e) -> e.getKey().run());
            workQueue.removeAll(actions);
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        //SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus())
        event.enqueueWork(() -> {
            LOGGER.info("[Insanitymod] Loading config...");
            ShieldTweaks.loadConfig();
        });

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