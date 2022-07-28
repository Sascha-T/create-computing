package de.saschat.createcomputing.tiles.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.logistics.trains.ITrackBlock;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour.RenderedTrackOverlayType;
import com.simibubi.create.foundation.tileEntity.renderer.SmartTileEntityRenderer;
import de.saschat.createcomputing.behaviour.tile.TrainNetworkObserver;
import de.saschat.createcomputing.tiles.TrainNetworkObserverTile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TrainNetworkObserverRenderer extends SmartTileEntityRenderer<TrainNetworkObserverTile> {

    public TrainNetworkObserverRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(TrainNetworkObserverTile te, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                              int light, int overlay) {
        super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
        BlockPos pos = te.getBlockPos();

        TrackTargetingBehaviour<TrainNetworkObserver> target = te.edgePoint;
        BlockPos targetPosition = target.getGlobalPosition();
        Level level = te.getLevel();
        BlockState trackState = level.getBlockState(targetPosition);
        Block block = trackState.getBlock();

        if (!(block instanceof ITrackBlock))
            return;

        ms.pushPose();
        ms.translate(-pos.getX(), -pos.getY(), -pos.getZ());
        RenderedTrackOverlayType type = RenderedTrackOverlayType.OBSERVER;
        TrackTargetingBehaviour.render(level, targetPosition, target.getTargetDirection(), target.getTargetBezier(), ms,
            buffer, light, overlay, type, 1);
        ms.popPose();

    }

}
