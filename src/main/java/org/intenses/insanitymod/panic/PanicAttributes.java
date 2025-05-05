package org.intenses.insanitymod.panic;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.intenses.insanitymod.Insanitymod;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PanicAttributes {
    public static float clientPanic = 0;
    public static final HashMap<RegistryObject<Attribute>, UUID> UUIDS = new HashMap<>();
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Insanitymod.MOD_ID);

    // Регистрация атрибутов с UUID
    public static final RegistryObject<Attribute> PANIC = registerAttribute(
            "panic",
            id -> new RangedAttribute(id, 0.0, 0.0, 500.0).setSyncable(true),
            "d4b3a7c0-5f9d-4b7d-9b7a-1e8f3b7a9b7e"
    );

    public static final RegistryObject<Attribute> MAX_PANIC = registerAttribute(
            "max_panic",
            id -> new RangedAttribute(id, 100.0, 10.0, 500.0).setSyncable(true),
            "a2c9e8d7-3b1f-4e75-8d9a-6f4b3c7d8e9f"
    );

    private static RegistryObject<Attribute> registerAttribute(
            String name,
            Function<String, Attribute> attribute,
            String uuid
    ) {
        return registerAttribute(name, attribute, UUID.fromString(uuid));
    }

    private static RegistryObject<Attribute> registerAttribute(
            String name,
            Function<String, Attribute> attribute,
            UUID uuid
    ) {
        RegistryObject<Attribute> registryObject = ATTRIBUTES.register(
                name,
                () -> attribute.apply("attribute." + Insanitymod.MOD_ID + "." + name)
        );
        UUIDS.put(registryObject, uuid);
        return registryObject;
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            if (entityType == EntityType.PLAYER) {
                ATTRIBUTES.getEntries().forEach(entry -> {
                    event.add(entityType, entry.get());
                });
            }
        }
    }
}