package de.saschat.createcomputing.tiles;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.components.structureMovement.ITransformableTE;
import com.simibubi.create.content.contraptions.components.structureMovement.StructureTransform;
import com.simibubi.create.content.logistics.trains.GraphLocation;
import com.simibubi.create.content.logistics.trains.TrackGraphHelper;
import com.simibubi.create.content.logistics.trains.management.edgePoint.EdgePointType;
import com.simibubi.create.content.logistics.trains.management.edgePoint.TrackTargetingBehaviour;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import dan200.computercraft.shared.Capabilities;
import de.saschat.createcomputing.CreateComputingMod;
import de.saschat.createcomputing.Registries;
import de.saschat.createcomputing.behaviour.tile.TrainNetworkObserver;
import de.saschat.createcomputing.peripherals.TrainNetworkObserverPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TrainNetworkObserverTile extends SmartTileEntity implements ITransformableTE {
    public TrackTargetingBehaviour<TrainNetworkObserver> edgePoint;
    public static final EdgePointType<TrainNetworkObserver> NETWORK_OBSERVER = EdgePointType.register(new ResourceLocation(
        CreateComputingMod.MOD_ID,
        "network_observer"
    ), TrainNetworkObserver::new);

    public GraphLocation getGraphLocation() {
        GraphLocation l = null;
        if (edgePoint.getTargetBezier() != null) {
            l = TrackGraphHelper.getBezierGraphLocationAt(getLevel(), edgePoint.getGlobalPosition(), Direction.AxisDirection.POSITIVE, edgePoint.getTargetBezier());
        } else {
            List<Vec3> trackAxes = AllBlocks.TRACK.get().getTrackAxes(level, edgePoint.getGlobalPosition(), edgePoint.getTrackBlockState());
            if(trackAxes.size() != 1) {
                getLevel().destroyBlock(getBlockPos(), true);
                return null;
            }
            l = TrackGraphHelper.getGraphLocationAt(level, edgePoint.getGlobalPosition(), Direction.AxisDirection.POSITIVE, trackAxes.get(0));
        }
        return l;
    }

    public LazyOptional<TrainNetworkObserverPeripheral> peripheral = LazyOptional.empty();

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == Capabilities.CAPABILITY_PERIPHERAL && side == Direction.UP) {
            if(!peripheral.isPresent())
                peripheral = LazyOptional.of(() -> new TrainNetworkObserverPeripheral(this));
            return peripheral.cast();
        }
        return super.getCapability(cap, side);
    }

    public TrainNetworkObserverTile(BlockPos p_155229_, BlockState p_155230_) {
        super(Registries.TRAIN_NETWORK_OBSERVER_TILE.get(), p_155229_, p_155230_);
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {
        behaviours.add(edgePoint = new TrackTargetingBehaviour<>(this, NETWORK_OBSERVER));
    }

    @Override
    public void transform(StructureTransform structureTransform) {
        edgePoint.transform(structureTransform);
    }
}
