package org.intenses.insanitymod.Items;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.network.chat.Component;
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
import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.config.ShieldConfig;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class ShieldTweaks {



    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FMLPaths.CONFIGDIR.get().toFile(), "insanitymod_shields.json");

    public static ShieldConfig shieldConfig;

    // Уникальные UUID для каждого атрибута
    private static final UUID ATTACK_SPEED_UUID = UUID.fromString("57038186-7ef2-4549-ab82-9029b04e2693");
    private static final UUID MOVEMENT_SPEED_UUID = UUID.fromString("3d616c36-aeb0-4a3d-ac33-c022b85c5d7a"); // Один UUID для всей скорости

    public ShieldTweaks() {
        System.out.println("[ShieldTweaks] ShieldTweaks instance created!");
    }

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            System.out.println("[ShieldTweaks] Файл конфига не найден, создаем новый.");
            createDefaultConfig();
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            shieldConfig = GSON.fromJson(reader, ShieldConfig.class);
            if (shieldConfig == null) {
                System.out.println("[ShieldTweaks] Ошибка загрузки JSON, создаем пустой конфиг.");
                shieldConfig = new ShieldConfig();
            } else {
                System.out.println("[ShieldTweaks] Конфиг загружен: " + GSON.toJson(shieldConfig));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultConfig() {
        shieldConfig = new ShieldConfig();
        shieldConfig.Shields.add(new ShieldConfig.Shield("minecraft:shield", -10, -4, -2));

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(shieldConfig, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;


        LivingEntity entity = event.player;
        if (shieldConfig == null) {
            System.out.println("[ShieldTweaks] Config not loaded!");
            return;
        }

        AttributeInstance attackSpeed = entity.getAttribute(Attributes.ATTACK_SPEED);
        AttributeInstance movementSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attackSpeed == null || movementSpeed == null) {
            return;
        }


        attackSpeed.removeModifier(ATTACK_SPEED_UUID);
        movementSpeed.removeModifier(MOVEMENT_SPEED_UUID);

        double totalAttackSpeedMod = 0;
        double totalMovementSpeedMod = 0;

        for (ShieldConfig.Shield shield : shieldConfig.Shields) {
            boolean hasItemInHand = hasItemInHand(entity, shield);
            boolean hasItemInHotbar = hasItemInHotbar(entity, shield);

            if (hasItemInHand) {
                totalAttackSpeedMod += shield.attackSpeedModifier;
                totalMovementSpeedMod += shield.movementSpeedModifierHand;
            }
            if (hasItemInHotbar) {
                totalMovementSpeedMod += shield.movementSpeedModifierHotbar;
            }
        }

        if (totalAttackSpeedMod != 0) {
            attackSpeed.addTransientModifier(new AttributeModifier(ATTACK_SPEED_UUID, "insanity_atk_speed", totalAttackSpeedMod, AttributeModifier.Operation.ADDITION));
        }
        if (totalMovementSpeedMod != 0) {
            movementSpeed.addTransientModifier(new AttributeModifier(MOVEMENT_SPEED_UUID, "insanity_mvmnt_speed", totalMovementSpeedMod, AttributeModifier.Operation.ADDITION));
        }
    }

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (shieldConfig == null) return;

        ItemStack stack = event.getItemStack();
        String itemId = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();

        for (ShieldConfig.Shield shield : shieldConfig.Shields) {
            if (itemId.equals(shield.id)) {
                List<Component> tooltip = event.getToolTip();
                tooltip.add(Component.literal("Attack Speed: " + shield.attackSpeedModifier + " when held").withStyle(style -> style.withColor(0xB22222).withItalic(true)));
                tooltip.add(Component.literal("Movement Speed: " + shield.movementSpeedModifierHand + " when held").withStyle(style -> style.withColor(0xB22222).withItalic(true)));
                tooltip.add(Component.literal("Movement Speed: " + shield.movementSpeedModifierHotbar + " when on back").withStyle(style -> style.withColor(0xB22222).withItalic(true)));
                break;
            }
        }
    }

    private boolean hasItemInHand(LivingEntity entity, ShieldConfig.Shield shield) {
        ItemStack mainHand = entity.getMainHandItem();
        ItemStack offHand = entity.getOffhandItem();
        return isMatchingItem(mainHand, shield) || isMatchingItem(offHand, shield);
    }

    private boolean hasItemInHotbar(LivingEntity entity, ShieldConfig.Shield shield) {
        if (!(entity instanceof Player player)) {
            return false;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (isMatchingItem(stack, shield)) {
                return true;
            }
        }
        return false;
    }

    private boolean isMatchingItem(ItemStack stack, ShieldConfig.Shield shield) {
        if (stack.isEmpty()) {
            return false;
        }
        String itemID = ForgeRegistries.ITEMS.getKey(stack.getItem()).toString();
        return itemID.equals(shield.id);
    }
}