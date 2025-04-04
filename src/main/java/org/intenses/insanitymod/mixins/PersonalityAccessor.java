package org.intenses.insanitymod.mixins;

import com.teamabnormals.personality.core.Personality;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import java.util.UUID;

@Mixin(value = Personality.class, remap = false)
public interface PersonalityAccessor {
    @Accessor("SITTING_PLAYERS")
    static Set<UUID> getSittingPlayers() {
        throw new AssertionError();
    }

    @Accessor("SYNCED_SITTING_PLAYERS")
    static Set<UUID> getSyncedSittingPlayers() {
        throw new AssertionError();
    }
}
