package de.saschat.createcomputing.blocks;

import com.simibubi.create.foundation.block.ITE;
import de.saschat.createcomputing.Registries;
import de.saschat.createcomputing.tiles.ComputerizedRedstoneLinkTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ComputerizedRedstoneLinkBlock extends Block implements ITE<ComputerizedRedstoneLinkTile> {
    public ComputerizedRedstoneLinkBlock() {
        super(BlockBehaviour.Properties.of(Material.WOOD));
    }

    @Override
    public Class<ComputerizedRedstoneLinkTile> getTileEntityClass() {
        return ComputerizedRedstoneLinkTile.class;
    }

    @Override
    public BlockEntityType<? extends ComputerizedRedstoneLinkTile> getTileEntityType() {
        return Registries.COMPUTERIZED_REDSTONE_LINK_TILE.get();
    }


    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return box(
            1,0,1,15,2,15
        );
    }
}
