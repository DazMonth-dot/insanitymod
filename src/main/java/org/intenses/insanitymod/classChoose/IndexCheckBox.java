package org.intenses.insanitymod.classChoose;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
//TODO:добавить изменение иконок, сдвинуть иконки чтобы была галочка

public class IndexCheckBox extends Checkbox {
    private final int index;
    private final GUI parentGui;
    private final int column;
    private Item item;

    protected IndexCheckBox(int x, int y, int width, int height, Component message, boolean selected, int index, int column, GUI parentGui,Item item) {
        super(x, y, width, height, message, selected);
        this.index = index;
        this.parentGui = parentGui;
        this.column=column;
        this.item = item;
    }

    public void setItem(Item item) {
        if (this.column == 2 || this.column == 3) {
            this.item = item;
        }
    }

    public void setVisible(boolean visible){
        this.visible = visible;
    }

    public void setActive(boolean active){
        this.active=active;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void forceSetSelected(boolean value) {
        if (this.selected() != value) {
            super.onPress();
        }
    }

    @Override
    public void onPress() {
        switch(this.column){
            case 1:
                parentGui.onLeftCheckboxPressed(index, this);
                break;
            case 2:
                parentGui.onRightCheckboxPressed(index, this);
                break;
            case 3:
                parentGui.onSorcererCBPress(index, this);
                break;
        }
    }

    @Override
    public void renderButton(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        if (this.item != null && this.visible) {
            var itemRenderer = Minecraft.getInstance().getItemRenderer();
            int iconX = this.x - 20;
            int iconY = this.y + (this.height - 16) / 2;

            itemRenderer.renderAndDecorateItem(this.item.getDefaultInstance(), iconX, iconY);
        }

        super.renderButton(poseStack, mouseX, mouseY, partialTick);
    }
}