package org.intenses.insanitymod.mixins;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class forgetScreens {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof io.github.apace100.origins.screen.OriginDisplayScreen ||
                screen instanceof  io.github.apace100.origins.screen.ChooseOriginScreen) {
            ci.cancel();
        }

    }
}
