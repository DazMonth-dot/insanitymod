package org.intenses.insanitymod.Items;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.intenses.insanitymod.config.ShieldConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class ShieldTweaks {
    private static final Logger LOGGER = Logger.getLogger(ShieldTweaks.class.getName());
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "insanitycore_shields.json");

    public static ShieldConfig shieldConfig;
    private static final Map<String, ShieldConfig.Shield> SHIELD_LOOKUP = new HashMap<>();

    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("57038186-7ef2-4549-ab82-9029b04e2693");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("3d616c36-aeb0-4a3d-ac33-c022b85c5d7a");

    public ShieldTweaks() {
        System.out.println("[ShieldTweaks] ShieldTweaks instance created!");
        loadConfig();
    }

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            LOGGER.info("[ShieldTweaks] Config file not found, creating default.");
            createDefaultConfig();
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            shieldConfig = GSON.fromJson(reader, ShieldConfig.class);
            if (shieldConfig == null) {
                LOGGER.warning("[ShieldTweaks] Failed to load config, creating default.");
                createDefaultConfig();
            } else {

                for (ShieldConfig.Shield shield : shieldConfig.Shields) {
                    if (!ForgeRegistries.ITEMS.containsKey(new ResourceLocation(shield.id))) {
                        LOGGER.warning("[ShieldTweaks] Invalid item ID in config: " + shield.id);
                        continue;
                    }

                    if (shield.attackSpeedModifier < -1.0 || shield.attackSpeedModifier > 1.0 ||
                            shield.movementSpeedModifierHand < -1.0 || shield.movementSpeedModifierHand > 1.0 ||
                            shield.movementSpeedModifierHotbar < -1.0 || shield.movementSpeedModifierHotbar > 1.0) {
                        LOGGER.warning("[ShieldTweaks] Invalid modifier values for shield " + shield.id + ", must be between -1.0 and 1.0");
                        continue;
                    }
                    SHIELD_LOOKUP.put(shield.id, shield);
                }
                LOGGER.info("[ShieldTweaks] Config loaded: " + GSON.toJson(shieldConfig));
            }
        } catch (IOException e) {
            LOGGER.severe("[ShieldTweaks] Error loading config: " + e.getMessage());
            createDefaultConfig();
        }
    }

    private static void createDefaultConfig() {
        shieldConfig = new ShieldConfig();
        // Use percentage values: -0.1 = -10%, -0.04 = -4%, -0.02 = -2%
        shieldConfig.Shields.add(new ShieldConfig.Shield("minecraft:shield", -0.1, -0.04, -0.02));
        SHIELD_LOOKUP.put("minecraft:shield", shieldConfig.Shields.get(0));

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(shieldConfig, writer);
        } catch (IOException e) {
            LOGGER.severe("[ShieldTweaks] Error creating default config: " + e.getMessage());
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        if (shieldConfig == null) {
            LOGGER.warning("[ShieldTweaks] Config not loaded!");
            return;
        }

        LivingEntity entity = event.player;
        AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
        AttributeInstance movementSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attackSpeed == null || movementSpeed == null) {
            return;
        }

        // Remove existing modifiers
        attackSpeed.removeModifier(ATTACK_SPEED_UUID);
        movementSpeed.removeModifier(MOVEMENT_SPEED_UUID);

        double totalAttackSpeedMod = 0;
        double totalMovementSpeedMod = 0;

        // Check hands first
        ItemStack mainHand = entity.getMainHandItem();
        ItemStack offHand = entity.getOffhandItem();
        ShieldConfig.Shield mainHandShield = getShieldFromStack(mainHand);
        ShieldConfig.Shield offHandShield = getShieldFromStack(offHand);

        if (mainHandShield != null) {
            totalAttackSpeedMod += mainHandShield.attackSpeedModifier;
            totalMovementSpeedMod += mainHandShield.movementSpeedModifierHand;
        }
        if (offHandShield != null) {
            totalAttackSpeedMod += offHandShield.attackSpeedModifier;
            totalMovementSpeedMod += offHandShield.movementSpeedModifierHand;
        }

        // Check hotbar, excluding the selected slot
        if (entity instanceof Player player) {
            int selectedSlot = player.getInventory().selected;
            for (int i = 0; i < 9; i++) {
                if (i == selectedSlot) continue; // Skip the selected slot (main hand)
                ItemStack stack = player.getInventory().getItem(i);
                ShieldConfig.Shield shield = getShieldFromStack(stack);
                if (shield != null) {
                    // Only apply hotbar movement speed modifier if the shield is not in hand
                    boolean isInHand = (stack == mainHand || stack == offHand);
                    if (!isInHand) {
                        totalMovementSpeedMod += shield.movementSpeedModifierHotbar;
                    }
                }
            }
        }

        // Apply percentage-based modifiers using MULTIPLY_BASE
        if (totalAttackSpeedMod != 0) {
            attackSpeed.addTransientModifier(new AttributeModifier(
                    ATTACK_SPEED_UUID,
                    "insanity_atk_speed",
                    totalAttackSpeedMod,
                    AttributeModifier.Operation.MULTIPLY_BASE
            ));
        }
        if (totalMovementSpeedMod != 0) {
            movementSpeed.addTransientModifier(new AttributeModifier(
                    MOVEMENT_SPEED_UUID,
                    "insanity_mvmnt_speed",
                    totalMovementSpeedMod,
                    AttributeModifier.Operation.MULTIPLY_BASE
            ));
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (shieldConfig == null) {
            LOGGER.warning("[ShieldTweaks] Config not loaded, cannot display tooltip!");
            return;
        }

        ItemStack stack = event.getItemStack();
        String itemId = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString();
        ShieldConfig.Shield shield = SHIELD_LOOKUP.get(itemId);

        if (shield != null) {
            List<Component> tooltip = event.getToolTip();
            // Convert to percentage for display (e.g., -0.1 -> "-10%")
            int attackSpeedPercent = (int) (shield.attackSpeedModifier * 100);
            int movementSpeedHandPercent = (int) (shield.movementSpeedModifierHand * 100);
            int movementSpeedHotbarPercent = (int) (shield.movementSpeedModifierHotbar * 100);

            tooltip.add(Component.literal("Attack Speed: " + (attackSpeedPercent >= 0 ? "+" : "") + attackSpeedPercent + "% when held")
                    .withStyle(style -> style.withColor(attackSpeedPercent >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED).withItalic(true)));
            tooltip.add(Component.literal("Movement Speed: " + (movementSpeedHandPercent >= 0 ? "+" : "") + movementSpeedHandPercent + "% when held")
                    .withStyle(style -> style.withColor(movementSpeedHandPercent >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED).withItalic(true)));
            tooltip.add(Component.literal("Movement Speed: " + (movementSpeedHotbarPercent >= 0 ? "+" : "") + movementSpeedHotbarPercent + "% when in hotbar")
                    .withStyle(style -> style.withColor(movementSpeedHotbarPercent >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED).withItalic(true)));
        }
    }


    private ShieldConfig.Shield getShieldFromStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        String itemID = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString();
        return SHIELD_LOOKUP.get(itemID);
    }
}