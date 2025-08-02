package org.intenses.insanitymod.classChoose;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.*;

public class ItemGroup {

    private static final List<Item> DEFAULT_ITEMS = List.of(
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:iron_sword"))),
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:bow"))),
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:crossbow"))),
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:shield"))),
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:trident"))),
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:flint_and_steel")))
    );

    private static final List<Item> SPELLS = List.of(
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:blaze_rod"))),
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:ender_pearl"))),
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:fire_charge"))),
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:snowball"))),
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:potion"))),
            Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:experience_bottle")))
    );

    private static final List<List<Item>> ARMOR = List.of(
            List.of(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:leather_helmet"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:leather_chestplate"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:leather_leggings"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:leather_boots")))
            ),
            List.of(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:chainmail_helmet"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:chainmail_chestplate"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:chainmail_leggings"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:chainmail_boots")))
            ),
            List.of(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:golden_helmet"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:golden_chestplate"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:golden_leggings"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:golden_boots")))
            ),
            List.of(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:iron_helmet"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:iron_chestplate"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:iron_leggings"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:iron_boots")))
            ),
            List.of(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:diamond_helmet"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:diamond_chestplate"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:diamond_leggings"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:diamond_boots")))
            )
    );

    private static final List<List<Item>> CLASS_ITEMS = List.of(
            List.of(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:wooden_sword"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:leather_helmet"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:apple"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:wooden_pickaxe"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:stick"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:torch")))
            ),
            List.of(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:stone_axe"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:chainmail_chestplate"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:bread"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:stone_pickaxe"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:arrow"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:torch")))
            ),
            List.of(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:golden_sword"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:golden_helmet"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:cooked_beef"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:golden_pickaxe"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:torch"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:gold_ingot")))
            ),
            List.of(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:iron_sword"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:iron_chestplate"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:baked_potato"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:iron_pickaxe"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:torch"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:iron_ingot")))
            ),
            List.of(
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:diamond_sword"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:diamond_chestplate"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:golden_apple"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:diamond_pickaxe"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:torch"))),
                    Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft:diamond")))
            )
    );

    private static final List<String> CLASSES = List.of(
            "insanity.class.deprived",
            "insanity.class.deserter",
            "insanity.class.explorer",
            "insanity.class.knight",
            "insanity.class.sorcerer"
    );

    public static List<String> getClasses(){
        return CLASSES;
    }


   public static List<Item> getClassItems(int index){
       return CLASS_ITEMS.get(index);
   }

   public static List<Item> getSpells(){
       return SPELLS;
   }

   public static List<Item> getDefaultItems(){
       return DEFAULT_ITEMS;
   }

   public static List<Item> getClassArmor(int index){
       return ARMOR.get(index);
   }
}