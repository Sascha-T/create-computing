package de.saschat.createcomputing.behaviour.target;

import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import com.simibubi.create.content.logistics.block.display.target.DisplayTarget;
import com.simibubi.create.content.logistics.block.display.target.DisplayTargetStats;
import de.saschat.createcomputing.tiles.ComputerizedDisplayTargetTile;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class TextPassBehaviour extends DisplayTarget {
    @Override
    public void acceptText(int line, List<MutableComponent> list, DisplayLinkContext displayLinkContext) {
        ComputerizedDisplayTargetTile tile = (ComputerizedDisplayTargetTile) displayLinkContext.getTargetTE();
        tile.acceptText(line, list, displayLinkContext);
    }

    @Override
    public DisplayTargetStats provideStats(DisplayLinkContext displayLinkContext) {
        ComputerizedDisplayTargetTile tile = (ComputerizedDisplayTargetTile) displayLinkContext.getTargetTE();
        return new DisplayTargetStats(tile.maxHeight, tile.maxHeight, this);
    }
}
