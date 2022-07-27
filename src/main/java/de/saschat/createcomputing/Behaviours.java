package de.saschat.createcomputing;

import com.simibubi.create.content.logistics.block.display.AllDisplayBehaviours;
import de.saschat.createcomputing.behaviour.source.TextDisplayBehaviour;
import de.saschat.createcomputing.behaviour.target.TextPassBehaviour;
import net.minecraft.resources.ResourceLocation;

public class Behaviours {
    public static void register() {
        AllDisplayBehaviours.assignTile(
            AllDisplayBehaviours.register(
                new ResourceLocation(CreateComputingMod.MOD_ID,
                    "computerized_display_source"),
                new TextDisplayBehaviour()
            ),
            Registries.COMPUTERIZED_DISPLAY_SOURCE_TILE.get().delegate
        );
        AllDisplayBehaviours.assignTile(
            AllDisplayBehaviours.register(
                new ResourceLocation(CreateComputingMod.MOD_ID,
                    "computerized_display_target"),
                new TextPassBehaviour()
            ),
            Registries.COMPUTERIZED_DISPLAY_TARGET_TILE.get().delegate
        );
    }
}
