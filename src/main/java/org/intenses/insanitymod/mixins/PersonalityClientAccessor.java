package org.intenses.insanitymod.mixins;

import com.teamabnormals.personality.client.PersonalityClient;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PersonalityClient.class)
public interface PersonalityClientAccessor {


    @Accessor("TOGGLE_SIT")
    static OptionInstance<Boolean> getToggleSit() {
        throw new UnsupportedOperationException();
    }


    @Accessor("TOGGLE_CRAWL")
    static OptionInstance<Boolean> getToggleCrawl() {
        throw new UnsupportedOperationException();
    }
}