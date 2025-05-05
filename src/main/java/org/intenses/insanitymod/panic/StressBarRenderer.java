package org.intenses.insanitymod.panic;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.intenses.insanitymod.Insanitymod;

@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, value = Dist.CLIENT)
public class StressBarRenderer {
    public static boolean RENDERING = false;
    private static final ResourceLocation ATLAS =
            new ResourceLocation("insanitymod", "textures/gui/stress_bar_atlas.png");

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Screen currentScreen = mc.screen;

        if (!(currentScreen instanceof InventoryScreen)) return;

        Player player = mc.player;
        if (player == null) return;

        float maxPanic = (float) player.getAttributeValue(PanicAttributes.MAX_PANIC.get());
        float currentPanic = PanicAttributes.clientPanic;
        float progress = maxPanic > 0 ? currentPanic / maxPanic : 0;

        int barWidth = 182;
        int barHeight = 19;

        PoseStack poseStack = event.getPoseStack();

        if (currentScreen instanceof AbstractContainerScreen<?> containerScreen) {
            int guiLeft = containerScreen.getGuiLeft();
            int guiTop = containerScreen.getGuiTop();

            int guiWidth = containerScreen.getXSize();
            int x = guiLeft + (guiWidth - barWidth) / 2;
            int y = guiTop - barHeight - 6;
            if (RENDERING) {
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, ATLAS);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                GuiComponent.blit(poseStack, x, y, 0, 0, barWidth, barHeight, 182, 38);


                int filledWidth = (int) (progress * barWidth);
                if (filledWidth > 0) {
                    GuiComponent.blit(poseStack, x, y, 0, 19, filledWidth, barHeight, 182, 38);
                }

                RenderSystem.disableBlend();
            }
        }
    }
}
