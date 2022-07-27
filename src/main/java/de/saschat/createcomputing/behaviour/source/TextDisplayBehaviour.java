package de.saschat.createcomputing.behaviour.source;

import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import com.simibubi.create.content.logistics.block.display.source.DisplaySource;
import com.simibubi.create.content.logistics.block.display.target.DisplayTargetStats;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplayTileEntity;
import de.saschat.createcomputing.tiles.ComputerizedDisplaySourceTile;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

import java.util.List;

public class TextDisplayBehaviour extends DisplaySource {
    public static MutableComponent NIL_TEXT = new TextComponent("");
    @Override
    public List<MutableComponent> provideText(DisplayLinkContext displayLinkContext, DisplayTargetStats displayTargetStats) {
        /*
            Maybe some events in the future?
         */
        return ((ComputerizedDisplaySourceTile) displayLinkContext.getSourceTE()).getFromPos(displayLinkContext.te().getBlockPos()).toDisplay;
    }

    @Override
    public int getPassiveRefreshTicks() {
        return 20;
    }
}
