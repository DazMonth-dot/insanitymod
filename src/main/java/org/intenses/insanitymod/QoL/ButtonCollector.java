package org.intenses.insanitymod.QoL;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

public class ButtonCollector {

    public static List<Button> collectAllButtons(Screen screen) {
        List<Button> allButtons = new ArrayList<>();
        for (Widget widget : screen.renderables) {
            if (widget instanceof Button) {
                allButtons.add((Button) widget);
            } else if (widget instanceof AbstractWidget buttonWidget && buttonWidget.active) {
                if (buttonWidget instanceof Button) {
                    allButtons.add((Button) buttonWidget);
                }
            }
        }

        return allButtons;
    }
}
