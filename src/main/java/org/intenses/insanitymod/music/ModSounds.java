package org.intenses.insanitymod.music;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.intenses.insanitymod.Insanitymod;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Insanitymod.MOD_ID);

    public static final RegistryObject<SoundEvent> BACKWARD =
            registerSoundEvent("backward");
    public static final RegistryObject<SoundEvent> CONFIRM =
            registerSoundEvent("confirm");
    public static final RegistryObject<SoundEvent> FORWARD =
            registerSoundEvent("forward");
    public static final RegistryObject<SoundEvent> SKILL_POINT_LEARN =
            registerSoundEvent("skill_point_learn");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(Insanitymod.MOD_ID, name);

        return SOUND_EVENTS.register(name,() -> new SoundEvent(new ResourceLocation(Insanitymod.MOD_ID, name)));
    }
    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}