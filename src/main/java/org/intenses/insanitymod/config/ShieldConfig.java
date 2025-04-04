package org.intenses.insanitymod.config;
import java.util.ArrayList;
import java.util.List;

public class ShieldConfig {
    public List<Shield> Shields = new ArrayList<>();
    public static class Shield {
        public String id;
        public double attackSpeedModifier;
        public double movementSpeedModifierHand;
        public double movementSpeedModifierHotbar;

        public Shield(String id, double attackSpeedModifier, double movementSpeedModifierHand, double movementSpeedModifierHotbar) {
            this.id = id;
            this.attackSpeedModifier = attackSpeedModifier;
            this.movementSpeedModifierHand = movementSpeedModifierHand;
            this.movementSpeedModifierHotbar = movementSpeedModifierHotbar;
        }
    }
}