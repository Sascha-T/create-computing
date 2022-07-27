package de.saschat.createcomputing.peripherals.handles;

import com.google.gson.Gson;
import com.simibubi.create.content.logistics.block.display.DisplayLinkTileEntity;
import com.simibubi.create.content.logistics.block.display.target.DisplayTarget;
import com.simibubi.create.content.logistics.trains.management.display.FlapDisplayTileEntity;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.saschat.createcomputing.behaviour.source.TextDisplayBehaviour;
import de.saschat.createcomputing.peripherals.ComputerizedDisplaySourcePeripheral;
import de.saschat.createcomputing.tiles.ComputerizedDisplaySourceTile;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;

public class DisplayLinkHandle {
    public UUID id = UUID.randomUUID();
    public boolean open = true;
    public Direction direction;
    private ComputerizedDisplaySourcePeripheral parent;

    public DisplayLinkHandle(ComputerizedDisplaySourcePeripheral parent, Direction direction) {
        this.parent = parent;
        this.direction = direction;
    }

    public void checkOpen() throws LuaException {
        if (!open)
            throw new LuaException("Trying to use closed handle.");
    }

    public ComputerizedDisplaySourceTile.DisplayData getData() throws LuaException {
        if (!parent.source.display_links.containsKey(direction)) {
            close();
            checkOpen();
        }
        return parent.source.display_links.get(direction);
    }

    public void setData(ComputerizedDisplaySourceTile.DisplayData data) throws LuaException {
        if (!parent.source.display_links.containsKey(direction)) {
            close();
            checkOpen();
        }
        parent.source.display_links.put(direction, data);
    }

    @LuaFunction
    public final void close() throws LuaException {
        this.open = false;
        if (!this.parent.closeHandle(this)) {
            throw new RuntimeException("Failed to close handle.");
        }
    }

    public DisplayTarget getTarget() throws LuaException {
        ComputerizedDisplaySourceTile.DisplayData data = getData();
        return data.tileEntity.activeTarget;
    }

    @LuaFunction
    public final Object[] getTargetType(IArguments arg) throws LuaException {
        checkOpen();
        ComputerizedDisplaySourceTile.DisplayData data = getData();

        DisplayLinkTileEntity te = data.tileEntity;

        if(getTarget() != null)
            return new Object[]{getTarget().id.toString()};
        return new Object[]{null};
    }

    @LuaFunction
    public final void setText(IArguments arg) throws LuaException {
        checkOpen();

        Gson gson = new Gson();
        Map<Double, ?> table = (Map<Double, ?>) arg.getTable(0);
        List<MutableComponent> components = new ArrayList<>();
        for (Map.Entry<Double, ?> entry : table.entrySet()) {
            if (!(entry.getKey() instanceof Double)) {
                throw new LuaException("Invalid table index.");
            }
            if (!((entry.getValue()) instanceof Map))
                throw new LuaException("Table value should be a component (table).");
            try {
                for (int i = components.size(); i < entry.getKey() - 1; i++) {
                    components.add(TextDisplayBehaviour.NIL_TEXT);
                }
                components.add(
                    entry.getKey().intValue() - 1,
                    Component.Serializer.fromJson(gson.toJson(entry.getValue()))
                );
            } catch (Exception ex) {
                throw new LuaException(ex.getMessage());
            }
        }

        ComputerizedDisplaySourceTile.DisplayData data = getData();
        data.toDisplay = components;
        setData(data);
    }


    @LuaFunction
    public final Object[] getText() throws LuaException {
        checkOpen();
        ComputerizedDisplaySourceTile.DisplayData data = getData();

        Gson gson = new Gson();
        Map<Double, Object> result = new HashMap<>();
        int i = 0;
        for (MutableComponent cmp : data.toDisplay) {
            if (!cmp.equals(TextDisplayBehaviour.NIL_TEXT))
                result.put((double) i, gson.fromJson(Component.Serializer.toJson(cmp), Map.class));
            i++;
        }

        return new Object[]{result};
    }

    @LuaFunction(mainThread = true)
    public final Object[] getWidth() throws LuaException {
        ComputerizedDisplaySourceTile.DisplayData data = getData();
        if (getTarget() != null) {
            switch (getTarget().id.toString()) {
                // @todo more target sizes
                case "create:display_board_target": {
                    FlapDisplayTileEntity d = (FlapDisplayTileEntity) data.tileEntity.getLevel().getBlockEntity(data.tileEntity.getTargetPosition());
                    d = d.getController();
                    return new Object[]{d.getMaxCharCount()};
                }
            }
        }
        return new Object[]{-1};
    }

    @LuaFunction(mainThread = true)
    public final Object[] getHeight() throws LuaException {
        ComputerizedDisplaySourceTile.DisplayData data = getData();
        if (getTarget() != null) {
            switch (getTarget().id.toString()) {
                // @todo more target sizes
                case "create:display_board_target": {
                    FlapDisplayTileEntity d = (FlapDisplayTileEntity) data.tileEntity.getLevel().getBlockEntity(data.tileEntity.getTargetPosition());
                    d = d.getController();
                    return new Object[]{d.getLines().size()};
                }
            }
        }

        return new Object[]{-1};
    }

}
