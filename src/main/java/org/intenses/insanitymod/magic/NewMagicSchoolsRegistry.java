package org.intenses.insanitymod.magic;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.intenses.insanitymod.Insanitymod;
import org.jetbrains.annotations.NotNull;


public class NewMagicSchoolsRegistry extends SchoolRegistry {

    public static final TagKey<Item> ICETP_FOCUS = ItemTags.create(new ResourceLocation(Insanitymod.MOD_ID, "icetp_focus"));
    public static final TagKey<Item> BLOODTP_FOCUS = ItemTags.create(new ResourceLocation(Insanitymod.MOD_ID, "bloodtp_focus"));
    public static final TagKey<Item> ENDTP_FOCUS = ItemTags.create(new ResourceLocation(Insanitymod.MOD_ID, "endtp_focus"));
    public static final TagKey<Item> RESTORATION_FOCUS = ItemTags.create(new ResourceLocation(Insanitymod.MOD_ID, "restoration_focus"));

    private static final DeferredRegister<SchoolType> INSANITY_SCHOOLS = DeferredRegister.create(SCHOOL_REGISTRY_KEY, Insanitymod.MOD_ID);

    public static void register(IEventBus eventBus) {
        INSANITY_SCHOOLS.register(eventBus);
        Insanitymod.LOGGER.info("Schools registered: {}", INSANITY_SCHOOLS.getEntries().stream().map(RegistryObject::getId).toList());
    }

    private static RegistryObject<SchoolType> registerSchool(SchoolType type) {
        return INSANITY_SCHOOLS.register(type.getId().getPath(), () -> type);
    }

    private static ResourceLocation id(@NotNull String path) {
        return new ResourceLocation(Insanitymod.MOD_ID, path);
    }

    public static final ResourceLocation ICETP_SOURCE = id("icetp");
    public static final ResourceLocation BLOODTP_SOURCE = id("bloodtp");
    public static final ResourceLocation ENDTP_SOURCE = id("endtp");
    public static final ResourceLocation RESTORATION_SOURCE = id("restorations");

    public static final RegistryObject<SchoolType> ICETP = registerSchool(new SchoolType
            (
                    ICETP_SOURCE,
                    ICETP_FOCUS,
                    Component.translatable("school.insanitymod.icetp").withStyle(Style.EMPTY.withColor(13695487)),
                    LazyOptional.of(AtributesInfo.ICETP_MAGIC_POWER::get),
                    LazyOptional.of(AtributesInfo.ICETP_MAGIC_RESIST::get),
                    LazyOptional.of(SoundRegistry.EVOCATION_CAST::get)
            ));
    public static final RegistryObject<SchoolType> ENDTP = registerSchool(new SchoolType
            (
                    ENDTP_SOURCE,
                    ENDTP_FOCUS,
                    Component.translatable("school.insanitymod.endtp").withStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE)),
                    LazyOptional.of(AtributesInfo.ENDTP_MAGIC_POWER::get),
                    LazyOptional.of(AtributesInfo.ENDTP_MAGIC_RESIST::get),
                    LazyOptional.of(SoundRegistry.EVOCATION_CAST::get)
            ));
    public static final RegistryObject<SchoolType> BLOODTP = registerSchool(new SchoolType
            (
            BLOODTP_SOURCE,
            BLOODTP_FOCUS,
            Component.translatable("school.insanitymod.bloodtp").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)),
            LazyOptional.of(AtributesInfo.BLOODTP_MAGIC_POWER::get),
            LazyOptional.of(AtributesInfo.BLOODTP_MAGIC_RESIST::get),
            LazyOptional.of(SoundRegistry.EVOCATION_CAST::get)
    ));
    public static final RegistryObject<SchoolType> RESTORATION = registerSchool(new SchoolType
            (
                    RESTORATION_SOURCE,
                    RESTORATION_FOCUS,
                    Component.translatable("school.insanitymod.restoration").withStyle(Style.EMPTY.withColor(16775380)),
                    LazyOptional.of(AtributesInfo.RESTORATION_MAGIC_POWER::get),
                    LazyOptional.of(AtributesInfo.RESTORATION_MAGIC_RESIST::get),
                    LazyOptional.of(SoundRegistry.EVOCATION_CAST::get)
            ));


}