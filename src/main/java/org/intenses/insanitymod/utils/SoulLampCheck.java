package org.intenses.insanitymod.utils;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.tags.ItemTags;
import top.theillusivec4.curios.api.CuriosApi;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Mod.EventBusSubscriber(modid = "insanitymod", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SoulLampCheck {

    private static final ResourceLocation VEILED_EFFECT_ID = new ResourceLocation("engulfingdarkness", "veiled");
    private static final ResourceLocation SAFE_TAG = new ResourceLocation("edl", "safe");
    private static final ResourceLocation WATER_SAFE_TAG = new ResourceLocation("edl", "water_safe");
    private static boolean lastHadSafeItem = false; // Состояние для отслеживания предыдущего состояния
    private static final int VEILED_DURATION = 999999; // Навсегда, как в SafeProcedure

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player == null) return;

        Player player = event.player;
        boolean inWater = player.isInWater();
        boolean hasSafeItem = hasSafeOrWaterSafeItem(player, inWater);
        int lightLevel = player.level.getBrightness(LightLayer.BLOCK, player.blockPosition());
        boolean inDarkness = lightLevel <= 4; // Порог темноты по умолчанию из Engulfing Darkness

        boolean shouldHaveVeiled = hasSafeItem && inDarkness;
        MobEffect veiledEffect = ForgeRegistries.MOB_EFFECTS.getValue(VEILED_EFFECT_ID);

        if (veiledEffect == null) return; // Эффект не зарегистрирован, выходим

        // Проверяем наличие эффекта
        boolean hasVeiled = player.hasEffect(veiledEffect);

        // Применяем или снимаем эффект только при изменении состояния
        if (shouldHaveVeiled && !hasVeiled) {
            // Применяем эффект навсегда, как в SafeProcedure
            player.addEffect(new MobEffectInstance(veiledEffect, VEILED_DURATION, 0, true, true));
            lastHadSafeItem = true;
        } else if (!shouldHaveVeiled && lastHadSafeItem && hasVeiled) {
            // Снимаем эффект только если он был добавлен нами
            player.removeEffect(veiledEffect);
            lastHadSafeItem = false;
        }
    }

    private static boolean hasSafeOrWaterSafeItem(Player player, boolean inWater) {
        // Проверяем основной слот, зачарованный слот, и голову, как в SafeProcedure
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        // Проверяем тег water_safe, если игрок в воде
        if (inWater) {
            if (mainHand.is(ItemTags.create(WATER_SAFE_TAG)) || offHand.is(ItemTags.create(WATER_SAFE_TAG)) || helmet.is(ItemTags.create(WATER_SAFE_TAG))) {
                return true;
            }
        }

        // Проверяем тег safe, если игрок не в воде
        if (mainHand.is(ItemTags.create(SAFE_TAG)) || offHand.is(ItemTags.create(SAFE_TAG)) || helmet.is(ItemTags.create(SAFE_TAG))) {
            return true;
        }

        // Проверяем Curios (например, necklace), как для SOUL_LANTERN
        return CuriosApi.getCuriosHelper()
                .findFirstCurio(player, Items.SOUL_LANTERN)
                .isPresent();
    }
}