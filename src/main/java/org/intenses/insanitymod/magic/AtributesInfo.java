package org.intenses.insanitymod.magic;

import io.redspace.ironsspellbooks.api.attribute.MagicRangedAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.intenses.insanitymod.Insanitymod;


@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD) // Add this
public class AtributesInfo {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Insanitymod.MOD_ID);

    public static final RegistryObject<Attribute> ICETP_MAGIC_RESIST = registerResistanceAttribute("icetp");
    public static final RegistryObject<Attribute> BLOODTP_MAGIC_RESIST = registerResistanceAttribute("bloodtp");
    public static final RegistryObject<Attribute> ENDTP_MAGIC_RESIST = registerResistanceAttribute("endtp");
    public static final RegistryObject<Attribute> RESTORATION_MAGIC_RESIST = registerResistanceAttribute("restoration");

    public static final RegistryObject<Attribute> ICETP_MAGIC_POWER = registerPowerAttribute("icetp");
    public static final RegistryObject<Attribute> BLOODTP_MAGIC_POWER = registerPowerAttribute("bloodtp");
    public static final RegistryObject<Attribute> ENDTP_MAGIC_POWER = registerPowerAttribute("endtp");
    public static final RegistryObject<Attribute> RESTORATION_MAGIC_POWER = registerPowerAttribute("restoration");



    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event)
    {
        event.getTypes().forEach(entity -> {
            event.add(entity, ICETP_MAGIC_RESIST.get());
            event.add(entity, BLOODTP_MAGIC_RESIST.get());
            event.add(entity, ENDTP_MAGIC_RESIST.get());
            event.add(entity, RESTORATION_MAGIC_RESIST.get());

            event.add(entity, ICETP_MAGIC_POWER.get());
            event.add(entity, ENDTP_MAGIC_POWER.get());
            event.add(entity, BLOODTP_MAGIC_POWER.get());
            event.add(entity, RESTORATION_MAGIC_POWER.get());
        });
    }

    // ;_;
    private static RegistryObject<Attribute> registerResistanceAttribute(String id)
    {
        return ATTRIBUTES.register(id + "_magic_resist", () ->
                (new MagicRangedAttribute("attribute.insanity." + id + "_magic_resist",
                        1.0D, 0, 10).setSyncable(true)));
    }

    private static RegistryObject<Attribute> registerPowerAttribute(String id)
    {
        return ATTRIBUTES.register(id + "_spell_power", () ->
                (new MagicRangedAttribute("attribute.insanity." + id + "_spell_power",
                        1.0D, 0, 10).setSyncable(true)));
    }

    public static void register(IEventBus eventBus)
    {
        ATTRIBUTES.register(eventBus);
        Insanitymod.LOGGER.info("Attributes registered: {}", ATTRIBUTES.getEntries().stream().map(RegistryObject::getId).toList());
    }
}