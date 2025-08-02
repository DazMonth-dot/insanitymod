package org.intenses.insanitymod.QoL;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;

import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.resource.PathPackResources;
import org.intenses.insanitymod.Insanitymod;

import java.io.IOException;


@Mod.EventBusSubscriber(modid = Insanitymod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class AddPackFinderEventTest {
    public static final String MODID = Insanitymod.MOD_ID;


    private static void generateRP(AddPackFindersEvent event, String resourceName, String displayName, Pack.Position position, boolean notVisible ){
        try {
            if (event.getPackType() == PackType.CLIENT_RESOURCES) {

                var resourcePath = ModList.get().getModFileById(MODID).getFile().findResource(resourceName);
                var pack = new PathPackResources(ModList.get().getModFileById(MODID).getFile().getFileName() + ":" + resourcePath, resourcePath);

                var metadataSection = pack.getMetadataSection(PackMetadataSection.SERIALIZER);
                if (metadataSection != null) {
                    event.addRepositorySource((packConsumer, packConstructor) ->
                            packConsumer.accept(
                                    new Pack(
                                            "builtin/"+resourceName,
                                            true,
                                            () -> pack,
                                            Component.literal(displayName),
                                            metadataSection.getDescription(),
                                            PackCompatibility.COMPATIBLE,
                                            position,
                                            true,
                                            PackSource.BUILT_IN,
                                            notVisible
                                    )
                            )
                    );
                };
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load built-in resource pack", ex);
        }
    }



    @SubscribeEvent
    public static void addPackFinders(AddPackFindersEvent event) {
        generateRP(event,
                "insanitymod_core",
                "Insanity Core",
                 Pack.Position.TOP,
                false);

        generateRP(event,
                "insanitymod_ui",
                "Insanity GUI",
                Pack.Position.TOP,
                false);

        generateRP(event,
                "insanitymod_sounds",
                "Insanity Music",
                Pack.Position.TOP,
                false);

        generateRP(event,
                "insanitymod_shimmer",
                "Insanity Shimmer",
                Pack.Position.TOP,
                false);
    }
}