package org.intenses.insanitymod.classChoose;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.intenses.insanitymod.Insanitymod;

import java.util.List;

public class OriginsStuff {

    public static List<ResourceLocation> getAvailableOrigins() {
        Registry<Origin> registry = OriginsAPI.getOriginsRegistry();
        return registry.keySet().stream()
                .filter(key -> key != null && !key.getPath().isBlank()) // исключаем пустые ключи
                .toList();
    }

    public static void setPlayerOrigin(ServerPlayer player, ResourceLocation originId) {
        Registry<OriginLayer> layerRegistry = OriginsAPI.getLayersRegistry();
        Registry<Origin> originRegistry = OriginsAPI.getOriginsRegistry();

        ResourceLocation layerId = new ResourceLocation("origins", "origin");

        OriginLayer layer = layerRegistry.get(layerId);
        Origin origin = originRegistry.get(originId);

        if (layer == null || origin == null) {
            Insanitymod.LOGGER.warn("Layer or race not found: " + originId);
            return;
        }

        ResourceKey<OriginLayer> layerKey = layerRegistry.getResourceKey(layer)
                .orElse(ResourceKey.create(OriginsAPI.getLayersRegistry().key(), layerId));

        ResourceKey<Origin> originKey = originRegistry.getResourceKey(origin)
                .orElse(ResourceKey.create(OriginsAPI.getOriginsRegistry().key(), originId));

        player.getCapability(OriginsAPI.ORIGIN_CONTAINER).ifPresent(container -> {
            container.setOrigin(layerKey, originKey);
            container.checkAutoChoosingLayers(false);
            container.synchronize();
            container.onChosen(originKey, false);
        });
    }

    public static String getOriginName(ResourceLocation id) {
        Registry<Origin> registry = OriginsAPI.getOriginsRegistry();
        Origin origin = registry.get(id);
        if (origin != null) {
            return origin.getName().getString();
        }
        return "Unknown";
    }

    public static String getOriginDescription(ResourceLocation id) {
        Registry<Origin> registry = OriginsAPI.getOriginsRegistry();
        Origin origin = registry.get(id);
        if (origin != null) {
            return origin.getDescription().getString();
        }
        return "No description.";
    }
}
