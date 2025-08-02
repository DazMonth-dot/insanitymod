package org.intenses.insanitymod.classChoose;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.intenses.insanitymod.classChoose.GUI;
import org.jetbrains.annotations.NotNull;

public class raceButton extends Button {
    private final GUI parentGui;
    private final int position;

    public raceButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, int position, GUI parentGui) {
        super(pX, pY, pWidth, pHeight, pMessage, btn -> {
        });
        this.parentGui = parentGui;
        this.position = position;
    }

    @Override
    public void onPress() {
        switch (position) {
            case 1 -> parentGui.changeRaceLeft();
            case 2 -> parentGui.changeRaceRight();
        }
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (!this.visible) return;
        Component original = this.getMessage();
        this.setMessage(Component.empty());
        super.renderButton(poseStack, mouseX, mouseY, partialTick);
        this.setMessage(original);
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        float scale = 0.7f;
        poseStack.pushPose();
        poseStack.scale(scale, scale, 1f);

        float scaledX = (this.x + (this.width - font.width(this.getMessage()) * scale) / 2f) / scale;
        float scaledY = (this.y + (this.height - font.lineHeight * scale) / 2f) / scale;
        font.draw(poseStack, this.getMessage(), scaledX, scaledY, this.active ? 0xFFFFFF : 0xA0A0A0);

        poseStack.popPose();
    }
};