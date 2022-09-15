package de.saschat.createcomputing.tiles;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.logistics.block.display.DisplayLinkTileEntity;
import dan200.computercraft.shared.Capabilities;
import de.saschat.createcomputing.Registries;
import de.saschat.createcomputing.blocks.ComputerizedDisplaySourceBlock;
import de.saschat.createcomputing.peripherals.ComputerizedDisplaySourcePeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComputerizedDisplaySourceTile extends BlockEntity {
    public LazyOptional<ComputerizedDisplaySourcePeripheral> peripheral = LazyOptional.empty();
    public Map<Direction, DisplayData> display_links = new HashMap();

    public DisplayData getFromPos(BlockPos pos) {
        // @todo: optimize lol
        for(Direction d: Direction.values()) {
            if(getBlockPos().relative(d).equals(pos)) {
                return display_links.get(d);
            }
        }
        onNeighborChange(null, getLevel(), getBlockPos(), null);
        return getFromPos(pos);
    }

    public static class DisplayData {
        public DisplayLinkTileEntity tileEntity;
        public List<MutableComponent> toDisplay = new ArrayList<>();
        public DisplayData(DisplayLinkTileEntity te) {
            this.tileEntity = te;
        }
    }


    public String text;

    public ComputerizedDisplaySourceTile(BlockPos p_155229_, BlockState p_155230_) {
        super(Registries.COMPUTERIZED_DISPLAY_SOURCE_TILE.get(), p_155229_, p_155230_);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.CAPABILITY_PERIPHERAL && side == getBlockState().getValue(ComputerizedDisplaySourceBlock.FACING)) {
            if (peripheral == null || !peripheral.isPresent()) {
                peripheral = LazyOptional.of(() -> new ComputerizedDisplaySourcePeripheral(this));
            }
            return peripheral.cast();
        }
        return super.getCapability(cap, side);
    }

    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        for (Direction dir : Direction.values()) {
            BlockPos location = pos.relative(dir);
            BlockState blockState = level.getBlockState(location);
            if (blockState.is(AllBlocks.DISPLAY_LINK.get())) {
                display_links.put(dir, new DisplayData((DisplayLinkTileEntity) level.getBlockEntity(location)));
                addLink(dir);
            } else {
                if (display_links.containsKey(dir)) {
                    display_links.remove(dir);
                    delLink(dir);
                }
            }
        }
    }

    public void onBlockBreak() {
        if (peripheral.isPresent()) {
            for (Direction dir: Direction.values()) {
                if(display_links.containsKey(dir))
                    peripheral.resolve().get().delLink(dir);
                else
                    peripheral.resolve().get().closeHandle(dir);
            }
        }
    }

    public void addLink(Direction dir) {
        if (peripheral.isPresent())
            peripheral.resolve().get().addLink(dir);
    }

    public void delLink(Direction dir) {
        if (peripheral.isPresent())
            peripheral.resolve().get().delLink(dir);
    }
}
