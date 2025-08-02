package org.intenses.insanitymod.classChoose;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class VisibilityButton extends Button {
    private final GUI parentGui;
    private final int position;


    public VisibilityButton(int pX, int pY, int pWidth, int pHeight, Component pMessage,int position, GUI parentGui) {
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
    public void onPress() {
        switch(position){
            case(1) -> parentGui.chooseOneGroup(1);
            case(2) -> parentGui.chooseOneGroup(2);
            case(3) -> parentGui.chooseOneGroup(3);
        }
    }
};