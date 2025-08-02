package org.intenses.insanitymod.mixins;

import io.github.apace100.origins.screen.ChooseOriginScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ChooseOriginScreen.class,remap = false)
public class OriginsScreenDisabler {

    /**
     * @author DazMonth
     * @reason Removing GUI
     */
    @Overwrite
    protected void init(){
        return;
    }
}
