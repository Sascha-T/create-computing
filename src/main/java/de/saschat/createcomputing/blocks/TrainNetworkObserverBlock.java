package de.saschat.createcomputing.blocks;

import com.simibubi.create.foundation.block.ITE;
import de.saschat.createcomputing.Registries;
import de.saschat.createcomputing.tiles.TrainNetworkObserverTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class TrainNetworkObserverBlock extends Block implements ITE<TrainNetworkObserverTile> {
    public TrainNetworkObserverBlock() {
        super(Properties.of(Material.WOOD).destroyTime(1));
    }

    @Override
    public Class<TrainNetworkObserverTile> getTileEntityClass() {
        return TrainNetworkObserverTile.class;
    }

    @Override
    public BlockEntityType<? extends TrainNetworkObserverTile> getTileEntityType() {
        return Registries.TRAIN_NETWORK_OBSERVER_TILE.get();
    }


    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean p_60519_) {
        if (pState.hasBlockEntity() && (!pState.is(pNewState.getBlock()) || !pNewState.hasBlockEntity())) {
            pLevel.removeBlockEntity(pPos);
        }
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return Stream.of(
            Block.box(2,15,2,14,17,14),
            Block.box(0,0,0,16,2,16),
            Block.box(0,14,0,16,16,16),
            Block.box(1,2,1,15,14,15),
            Block.box(15,2,0,16,14,1),
            Block.box(15,2,15,16,14,16),
            Block.box(0,2,15,1,14,16),
            Block.box(0,2,0,1,14,1)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }
}
