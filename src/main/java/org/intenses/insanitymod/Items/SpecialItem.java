package org.intenses.insanitymod.Items;

import com.elenai.feathers.api.FeathersHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import org.intenses.insanitymod.Insanitymod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SpecialItem extends Item implements ICurio {

    private static final int STAMINA_COST = 2;
    private static final int DEBUFF_DURATION = 100;
    private static final int EFFECT_DURATION = 60;
    private static final int STRENGTH_DURATION = 60;

    public SpecialItem(Properties properties) {
        super(properties.rarity(Rarity.RARE));
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity entity) {
        if (entity instanceof Player player && "necklace".equals(identifier)) {
            ItemStack stack = getEquippedStack(player);
            if (!stack.isEmpty()) {
                boolean isActive = isActive(stack);
                int mode = normalizeMode(getMode(stack));
                if (isActive) {
                    applyEffects(player, mode, mode); // Передаём нормализованный режим
                } else {
                    removeEffects(player, mode);
                }
            }
        }
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext) {
        return "necklace".equals(slotContext.identifier());
    }

    @Override
    public boolean canEquip(String identifier, LivingEntity entity) {
        return "necklace".equals(identifier);
    }

    @Override
    public ItemStack getStack() {
        return new ItemStack(this);
    }

    public static void setActive(ItemStack stack, boolean active) {
        stack.getOrCreateTag().putBoolean("isActive", active);
    }

    public static void setMode(ItemStack stack, int mode) {
        stack.getOrCreateTag().putInt("mode", mode);
    }

    public static boolean isActive(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("isActive");
    }

    public static int getMode(ItemStack stack) {
        return stack.getOrCreateTag().getInt("mode");
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean isSelected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;

        boolean isActive = isActive(stack);
        int mode = normalizeMode(getMode(stack));

        if (isActive) {
            if (ModList.get().isLoaded("feathers") && player.tickCount % 5 == 0) { // Обновление каждые 5 тиков
                if (player instanceof ServerPlayer serverPlayer) {
                    int stamina = FeathersHelper.getFeathers(serverPlayer);
                    if (stamina >= STAMINA_COST) {
                        FeathersHelper.setFeathers(serverPlayer, stamina - STAMINA_COST);
                    } else {
                        setActive(stack, false);
                        removeEffects(player, mode);
                        applyDebuff(player);
                        return;
                    }
                }
            }
            applyEffects(player, mode, mode); // Передаём нормализованный режим
            // Защита эффекта силы
            if (mode == 0 && !player.hasEffect(MobEffects.DAMAGE_BOOST)) {
                applyEffects(player, mode, mode);
            }
        } else {
            removeEffects(player, mode);
        }
    }

    public void applyEffects(Player player, int previousMode, int newMode) {
        // Нормализуем режимы, чтобы убрать 3
        previousMode = normalizeMode(previousMode);
        newMode = normalizeMode(newMode);

        // Удаляем эффект только предыдущего режима, если он активен
        if (previousMode != newMode) {
            removeEffectForMode(player, previousMode);
        }

        // Проверяем, неактивен ли амулет, чтобы не применять эффекты
        ItemStack stack = getEquippedStack(player);
        if (stack.isEmpty() || !isActive(stack)) {
            return;
        }

        // Применяем эффект нового режима
        switch (newMode) {
            case 0 -> {
                MobEffectInstance strengthEffect = new MobEffectInstance(MobEffects.DAMAGE_BOOST, STRENGTH_DURATION, 1, false, false);
                player.addEffect(strengthEffect);
            }
            case 1 -> player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, EFFECT_DURATION, 0, false, false));
            case 2 -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, EFFECT_DURATION, 1, false, false));
        }
    }

    public void removeEffectForMode(Player player, int mode) {
        mode = normalizeMode(mode); // Нормализуем режим, чтобы убрать 3
        switch (mode) {
            case 0 -> {
                if (player.hasEffect(MobEffects.DAMAGE_BOOST)) {
                    player.removeEffect(MobEffects.DAMAGE_BOOST);
                }
            }
            case 1 -> {
                if (player.hasEffect(MobEffects.INVISIBILITY)) {
                    player.removeEffect(MobEffects.INVISIBILITY);
                }
            }
            case 2 -> {
                if (player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                    player.removeEffect(MobEffects.MOVEMENT_SPEED);
                }
            }
        }
    }

    public void removeEffects(Player player, int mode) {
        // Этот метод теперь используем только для совместимости с KeyHandler
        removeEffectForMode(player, mode);
    }

    private static void applyDebuff(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, DEBUFF_DURATION, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, DEBUFF_DURATION, 0));
    }

    private ItemStack getEquippedStack(Player player) {
        // Проверяем основную руку
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() == this && isActive(mainHand)) {
            return mainHand;
        }

        // Ищем SpecialItem только в слоте "necklace"
        return CuriosApi.getCuriosHelper()
                .findFirstCurio(player, this)
                .filter(result -> "necklace".equals(result.slotContext().identifier()))
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    // Метод для нормализации режима, чтобы убрать 3 и вернуть 0, 1, или 2
    public static int normalizeMode(int mode) {
        return mode % 3; // Возвращает 0, 1, или 2 для любых значений mode
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // Добавляем текущий режим
        int mode = normalizeMode(getMode(stack)); // Нормализуем режим для tooltip
        Component modeText = Component.translatable("item.insanitymod.special_item.mode." + switch (mode) {
            case 0 -> "invisibility";
            case 1 -> "speed";
            case 2 -> "strength";
            default -> "unknown";
        }).withStyle(style -> style
                .withColor(0xF1C232) // Синий цвет для режима
                .withBold(true)
                .withItalic(true));
        tooltip.add(modeText);

        // Получаем тексты биндов для активации и переключения режима
        KeyMapping activateKey = Insanitymod.ACTIVATE_KEY;
        KeyMapping switchKey = Insanitymod.SWITCH_MODE_KEY;
        String activateKeyText = activateKey.getTranslatedKeyMessage().getString().toUpperCase(); // Получаем текст клавиши (например, "G")
        String switchKeyText = switchKey.getTranslatedKeyMessage().getString().toUpperCase(); // Получаем текст клавиши (например, "H")

        // Добавляем подпись для активации
        tooltip.add(Component.translatable("item.insanitymod.special_item.activate_key", activateKeyText)
                .withStyle(style -> style
                        .withColor(0x808080)
                        .withItalic(true)
                        .withBold(true))); // Серый цвет для текста

        // Добавляем подпись для смены режима
        tooltip.add(Component.translatable("item.insanitymod.special_item.switch_key", switchKeyText)
                .withStyle(style -> style
                        .withColor(0x808080)
                        .withItalic(true)
                        .withBold(true))); // Серый цвет для текста
    }

}