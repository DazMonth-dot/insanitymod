package org.intenses.insanitymod.QoL;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.intenses.insanitymod.Insanitymod;


import java.util.Map;


@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientUtils {
    private static final String MODID = Insanitymod.MOD_ID;
    Minecraft mc = Minecraft.getInstance();

    public static final TagKey<Item> ARTRIFACT_WEAPON = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "artifact_weapon"));
    public static final TagKey<Item> DAGGER = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "dagger"));
    public static final TagKey<Item> GLAIVE = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "glaive"));
    public static final TagKey<Item> GREATAXE = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "greataxe"));
    public static final TagKey<Item> GREATHAMMER = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "greathammer"));
    public static final TagKey<Item> GREATSWORD = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "greatsword")); // проверь имя!
    public static final TagKey<Item> HALBERD = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "halberd"));
    public static final TagKey<Item> HAMMER = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "hammer"));
    public static final TagKey<Item> KATANA = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "katana"));
    public static final TagKey<Item> RUNIC_WEAPON = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "runic_weapon"));
    public static final TagKey<Item> SABER = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "saber"));
    public static final TagKey<Item> SCYTHE = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "scythe"));
    public static final TagKey<Item> TWINBLADE = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "twinblade"));
    public static final TagKey<Item> WARGLAIVE = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation(MODID, "warglaive"));

    private static final Map<TagKey<Item>, String> WEAPON_TAGS = Map.ofEntries(
            Map.entry(DAGGER, "dagger"),
            Map.entry(GLAIVE, "glaive"),
            Map.entry(GREATAXE, "greataxe"),
            Map.entry(GREATHAMMER, "greathammer"),
            Map.entry(GREATSWORD, "greatsword"),
            Map.entry(HALBERD, "halberd"),
            Map.entry(HAMMER, "hammer"),
            Map.entry(KATANA, "katana"),
            Map.entry(RUNIC_WEAPON, "rune"),
            Map.entry(SABER, "saber"),
            Map.entry(SCYTHE, "scythe"),
            Map.entry(TWINBLADE, "twinblade"),
            Map.entry(WARGLAIVE, "warglaive"),
            Map.entry(ARTRIFACT_WEAPON, "artifact")
    );

    private static boolean isInTag(ItemStack stack, TagKey<Item> tag) {
        if (stack == null) return false;
        var optionalTag = ForgeRegistries.ITEMS.tags().getTag(tag);
        return optionalTag != null && optionalTag.contains(stack.getItem());
    }

    public static boolean isArtifactWeapon(ItemStack stack) {
        return isInTag(stack, ARTRIFACT_WEAPON);
    }

    public static boolean isDagger(ItemStack stack) {
        return isInTag(stack, DAGGER);
    }

    public static boolean isGlaive(ItemStack stack) {
        return isInTag(stack, GLAIVE);
    }

    public static boolean isGreataxe(ItemStack stack) {
        return isInTag(stack, GREATAXE);
    }

    public static boolean isGreathammer(ItemStack stack) {
        return isInTag(stack, GREATHAMMER);
    }

    public static boolean isGreatsword(ItemStack stack) {
        return isInTag(stack, GREATSWORD);
    }

    public static boolean isHalberd(ItemStack stack) {
        return isInTag(stack, HALBERD);
    }

    public static boolean isHammer(ItemStack stack) {
        return isInTag(stack, HAMMER);
    }

    public static boolean isKatana(ItemStack stack) {
        return isInTag(stack, KATANA);
    }

    public static boolean isRunicWeapon(ItemStack stack) {
        return isInTag(stack, RUNIC_WEAPON);
    }

    public static boolean isSaber(ItemStack stack) {
        return isInTag(stack, SABER);
    }

    public static boolean isScythe(ItemStack stack) {
        return isInTag(stack, SCYTHE);
    }

    public static boolean isTwinblade(ItemStack stack) {
        return isInTag(stack, TWINBLADE);
    }

    public static boolean isWarglaive(ItemStack stack) {
        return isInTag(stack, WARGLAIVE);
    }

    public static String getWeaponTypeKey(ItemStack stack) {
        for (Map.Entry<TagKey<Item>, String> entry : WEAPON_TAGS.entrySet()) {
            if (isInTag(stack, entry.getKey())) {
                return entry.getValue();
            }
        }
        return "unknown";
    }

    public static Component getWeaponTypeName(ItemStack stack) {
        String key = getWeaponTypeKey(stack);
        return key.equals("unknown") ? Component.literal("Unknown") : Component.translatable("insanity.weapon." + key);
    }




}