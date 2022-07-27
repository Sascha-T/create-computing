package de.saschat.createcomputing.peripherals;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.saschat.createcomputing.CreateComputingMod;
import de.saschat.createcomputing.peripherals.handles.DisplayLinkHandle;
import de.saschat.createcomputing.tiles.ComputerizedDisplaySourceTile;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ComputerizedDisplaySourcePeripheral implements IDynamicPeripheral {
    public ComputerizedDisplaySourceTile source;
    public List<IComputerAccess> computers = new LinkedList<>();

    public ComputerizedDisplaySourcePeripheral(ComputerizedDisplaySourceTile computerizedDisplaySourceTile) {
        this.source = computerizedDisplaySourceTile;
    }

    @Override
    public void attach(@NotNull IComputerAccess computer) {
        computers.add(computer);
    }

    @Override
    public void detach(@NotNull IComputerAccess computer) {
        computers.remove(computer);
    }

    @NotNull
    @Override
    public String[] getMethodNames() {
        return new String[]{"getLink", "getLinks"};
    }

    @NotNull
    @Override
    public MethodResult callMethod(@NotNull IComputerAccess iComputerAccess, @NotNull ILuaContext iLuaContext, int i, @NotNull IArguments iArguments) throws LuaException {
        switch (i) {
            case 0: {
                String text = iArguments.getString(0);
                Direction direction = Direction.byName(text);
                if (direction == null)
                    throw new LuaException("Specified direction is not real.");
                if (!source.display_links.containsKey(direction))
                    throw new LuaException("Specified direction does not have an attached link.");
                return MethodResult.of(
                    createHandle(direction)
                );
            }
            case 1: {
                return MethodResult.of(source.display_links.keySet().stream().map(a -> a.toString()).collect(Collectors.toList()));
            }
        }
        return MethodResult.of();
    }

    @NotNull
    @Override
    public String getType() {
        return new ResourceLocation(CreateComputingMod.MOD_ID, "computerized_display_source").toString();
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    public List<DisplayLinkHandle> handles = new ArrayList<>();

    public DisplayLinkHandle createHandle(Direction dir) {
        DisplayLinkHandle handle = new DisplayLinkHandle(
            this,
            dir
        );
        handles.add(handle);
        return handle;
    }

    public void closeHandle(Direction direction) {
        for (DisplayLinkHandle handle : handles) {
            if (handle.direction == direction) {
                handle.open = false;
                handles.remove(handle);
            }
        }
    }

    public boolean closeHandle(DisplayLinkHandle toDelete) {
        for (DisplayLinkHandle handle : handles) {
            if (handle.id == toDelete.id) {
                handle.open = false;
                handles.remove(handle);
                return true;
            }
        }
        return false;
    }

    public void addLink(Direction dir) {
        computers.forEach(a -> a.queueEvent("display_link_added", dir.toString()));
    }

    public void delLink(Direction dir) {
        closeHandle(dir);
        computers.forEach(a -> a.queueEvent("display_link_removed", dir.toString()));
    }
}
