package de.saschat.createcomputing.tiles;

import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import dan200.computercraft.shared.Capabilities;
import de.saschat.createcomputing.Registries;
import de.saschat.createcomputing.blocks.ComputerizedDisplaySourceBlock;
import de.saschat.createcomputing.peripherals.ComputerizedDisplaySourcePeripheral;
import de.saschat.createcomputing.peripherals.ComputerizedDisplayTargetPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ComputerizedDisplayTargetTile extends BlockEntity {
    public int maxHeight = 4;
    public int maxWidth = 15;
    public LazyOptional<ComputerizedDisplayTargetPeripheral> peripheral = LazyOptional.empty();
    public ComputerizedDisplayTargetTile(BlockPos p_153215_, BlockState p_153216_) {
        super(Registries.COMPUTERIZED_DISPLAY_TARGET_TILE.get(), p_153215_, p_153216_);
    }

    public void acceptText(int line, List<MutableComponent> list, DisplayLinkContext displayLinkContext) {
        if(peripheral.isPresent())
            peripheral.resolve().get().acceptText(line, list, displayLinkContext);
    }
    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.CAPABILITY_PERIPHERAL && side == getBlockState().getValue(ComputerizedDisplaySourceBlock.FACING)) {
            if (peripheral == null || !peripheral.isPresent()) {
                peripheral = LazyOptional.of(() -> new ComputerizedDisplayTargetPeripheral(this));
            }
            return peripheral.cast();
        }
        return super.getCapability(cap, side);
    }

}
