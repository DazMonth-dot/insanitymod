package org.intenses.insanitymod.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class EngulfAddon {
    private static Set<String> underwaterItems = new HashSet<>();
    private static Set<String> modItems = new HashSet<>();
    private static Set<String> disabledDimensions = new HashSet<>();
    private static final ResourceLocation VEILED_EFFECT_ID = new ResourceLocation("engulfingdarkness", "veiled");

    public EngulfAddon() {
    }

    public void loadConfig() {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("insanity_engulfaddon.json");
        try {
            if (!Files.exists(configPath)) {
                JsonObject defaultConfig = new JsonObject();
                defaultConfig.add("underwaterItems", new JsonArray());
                defaultConfig.add("modItems", new JsonArray());
                defaultConfig.add("disabledDimensions", new JsonArray());
                Files.writeString(configPath, new Gson().toJson(defaultConfig));
            }
            String jsonStr = Files.readString(configPath);
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(jsonStr, JsonObject.class);
            underwaterItems = jsonArrayToSet(json.getAsJsonArray("underwaterItems"));
            modItems = jsonArrayToSet(json.getAsJsonArray("modItems"));
            disabledDimensions = jsonArrayToSet(json.getAsJsonArray("disabledDimensions"));
        } catch (Exception e) {
            System.out.println("Ошибка загрузки конфига: " + e.getMessage());
        }
    }

    private Set<String> jsonArrayToSet(JsonArray array) {
        Set<String> set = new HashSet<>();
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                set.add(array.get(i).getAsString());
            }
        }
        return set;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level.isClientSide) {
            Player player = event.player;
            boolean inWater = player.isInWater();
            String dimension = player.level.dimension().location().toString();
            if (!disabledDimensions.contains(dimension)) {
                int insanityValue = hasModItemInSlots(player);
                MobEffect veiledEffect = Registry.MOB_EFFECT.get(VEILED_EFFECT_ID);
                if (veiledEffect != null) {
                    MobEffectInstance currentEffect = player.getEffect(veiledEffect);

                    // Удаляем эффект в воде, если это не underwaterItem
                    if (inWater && insanityValue != 1) {
                        if (currentEffect != null) {
                            player.removeEffect(veiledEffect);
                            // Принудительно очищаем эффект из карты, если он остаётся
                            player.getActiveEffectsMap().remove(veiledEffect);
                            System.out.println("Эффект 'veiled' удалён (в воде): insanityValue = " + insanityValue);
                        }
                    }
                    // Применяем эффект только в нужных случаях
                    else {
                        if (insanityValue == 0 && !inWater) {
                            // Факел вне воды
                            if (currentEffect == null || currentEffect.getDuration() <= 5) {
                                player.addEffect(new MobEffectInstance(veiledEffect, 20, 0, false, false));
                                System.out.println("Эффект 'veiled' применён: факел вне воды, длительность 20 тиков");
                            }
                        } else if (insanityValue == 1) {
                            // Предмет из underwaterItems
                            if (currentEffect == null || currentEffect.getDuration() <= 5) {
                                player.addEffect(new MobEffectInstance(veiledEffect, 20, 0, false, false));
                                System.out.println("Эффект 'veiled' применён: underwaterItem, длительность 20 тиков");
                            }
                        } else if (insanityValue == -1) {
                            // Нет подходящих предметов
                            if (currentEffect != null) {
                                player.removeEffect(veiledEffect);
                                player.getActiveEffectsMap().remove(veiledEffect);
                                System.out.println("Эффект 'veiled' удалён (нет предметов): insanityValue = -1");
                            }
                        }
                    }

                    // Дополнительная проверка: если эффект бесконечный, исправляем
                    if (currentEffect != null && currentEffect.getDuration() > 1000) {
                        player.removeEffect(veiledEffect);
                        player.getActiveEffectsMap().remove(veiledEffect);
                        System.out.println("Обнаружен бесконечный эффект 'veiled' — удалён принудительно");
                    }
                } else {
                    System.out.println("Эффект 'engulfingdarkness:veiled' не найден! Убедитесь, что мод 'Engulfing Darkness' установлен и работает корректно.");
                }
            }
        }
    }

    private int hasModItemInSlots(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        isModItem(mainHand);
        isWaterSafe(mainHand);
        isModItem(offHand);
        isWaterSafe(offHand);
        if (mainHand.hasTag() && mainHand.getTag().contains("InsanityItem")) {
            return mainHand.getTag().getInt("InsanityItem");
        }
        if (offHand.hasTag() && offHand.getTag().contains("InsanityItem")) {
            return offHand.getTag().getInt("InsanityItem");
        }

        return CuriosApi.getCuriosHelper().getCuriosHandler(player).map(handler -> {
            for (String identifier : handler.getCurios().keySet()) {
                ICurioStacksHandler stacksHandler = handler.getCurios().get(identifier);
                for (int i = 0; i < stacksHandler.getSlots(); i++) {
                    ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                    isModItem(stack);
                    isWaterSafe(stack);
                    if (stack.hasTag() && stack.getTag().contains("InsanityItem")) {
                        return stack.getTag().getInt("InsanityItem");
                    }
                }
            }
            return -1;
        }).orElse(-1);
    }

    private boolean isModItem(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String itemId = Registry.ITEM.getKey(stack.getItem()).toString();
        if (modItems.contains(itemId)) {
            stack.getOrCreateTag().putInt("InsanityItem", 0);
            return true;
        }
        return false;
    }

    private boolean isWaterSafe(ItemStack stack) {
        if (stack.isEmpty()) return false;
        String itemId = Registry.ITEM.getKey(stack.getItem()).toString();
        if (underwaterItems.contains(itemId)) {
            stack.getOrCreateTag().putInt("InsanityItem", 1);
            return true;
        }
        return false;
    }
}