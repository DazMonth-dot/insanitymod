package org.intenses.insanitymod.QoL;

import org.intenses.insanitymod.Insanitymod;
import org.intenses.insanitymod.mixins.PersonalityAccessor;

import java.util.Set;
import java.util.UUID;

public class SittingPlayersHelper {
    public static boolean isPlayerSitting(UUID playerUUID) {
        try {
            Set<UUID> sitting = PersonalityAccessor.getSittingPlayers();
            Set<UUID> synced = PersonalityAccessor.getSyncedSittingPlayers();
            return sitting.contains(playerUUID) || synced.contains(playerUUID);
        } catch (Exception e) {
            Insanitymod.LOGGER.error("Failed to check sitting status", e);
            return false;
        }
    }
}