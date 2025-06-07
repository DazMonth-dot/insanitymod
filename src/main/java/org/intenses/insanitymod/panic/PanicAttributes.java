package org.intenses.insanitymod.panic;

import net.minecraft.resources.ResourceLocation;
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

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PanicAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Insanitymod.MOD_ID);

    public static final RegistryObject<Attribute> PANIC = ATTRIBUTES.register("panic",
            () -> new RangedAttribute("attribute." + Insanitymod.MOD_ID + ".panic", 0.0, 0.0, 500.0).setSyncable(true));

    public static final RegistryObject<Attribute> MAX_PANIC = ATTRIBUTES.register("max_panic",
            () -> new RangedAttribute("attribute." + Insanitymod.MOD_ID + ".max_panic", 100.0, 10.0, 500.0).setSyncable(true));

    public static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }

    @SubscribeEvent
    public static void onAttributeModify(EntityAttributeModificationEvent event) {
        if (PANIC.isPresent()) {
            event.add(EntityType.PLAYER, PANIC.get());
        }
        if (MAX_PANIC.isPresent()) {
            event.add(EntityType.PLAYER, MAX_PANIC.get());
        }
    }
}
