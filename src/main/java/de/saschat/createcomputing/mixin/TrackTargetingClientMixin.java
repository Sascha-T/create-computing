package de.saschat.createcomputing.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingClient;
import com.simibubi.create.foundation.render.SuperRenderTypeBuffer;
import de.saschat.createcomputing.tiles.TrainNetworkObserverTile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrackTargetingClient.class)
public class TrackTargetingClientMixin {
    @Shadow(remap = false) private static EdgePointType<?> lastType;

    @Inject(method = "render", at=@At("HEAD"), remap = false)
    private static void render(PoseStack ms, SuperRenderTypeBuffer buffer, CallbackInfo ci) {
        if(lastType == TrainNetworkObserverTile.NETWORK_OBSERVER)
            lastType = EdgePointType.OBSERVER;
    }
}
