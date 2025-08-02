package org.intenses.insanitymod.classChoose;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class CustomButton extends Button {
    private final GUI parentGui;
    private final int position;

    public CustomButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, OnPress pOnPress, int position, GUI parentGui) {
        super(pX, pY, pWidth, pHeight, pMessage, btn->{});
        this.parentGui = parentGui;
        this.position = position;
    }

    public void setVisible(boolean visible){
        this.visible = visible;
    }

    public void setActive(boolean active){
        this.active=active;
    }

    @Override
    public void onPress(){
        switch (position) {
            case (1) -> parentGui.classToLeft();
            case (2) -> parentGui.classToRight();
            case (3) -> parentGui.onDone();
        };
    }
}