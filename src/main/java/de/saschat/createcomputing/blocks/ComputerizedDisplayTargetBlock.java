package de.saschat.createcomputing.blocks;

import de.saschat.createcomputing.Utils;
import de.saschat.createcomputing.tiles.ComputerizedDisplayTargetTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ComputerizedDisplayTargetBlock extends Block implements EntityBlock {
    public static DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public ComputerizedDisplayTargetBlock() {
        super(Properties.of(Material.METAL));
        registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_49820_) {
        Direction a = p_49820_.getPlayer().isCrouching() ? p_49820_.getHorizontalDirection() : p_49820_.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, a);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        p_49915_.add(FACING);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new ComputerizedDisplayTargetTile(p_153215_, p_153216_);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return Utils.rotate(Direction.NORTH, p_60555_.getValue(FACING), Stream.of(
            Block.box(0, 0, 0, 16, 16, 5),
            Block.box(2, 2, -2, 14, 14, 0),
            Block.box(2, 2, 5, 14, 14, 10)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get());
    }
}
