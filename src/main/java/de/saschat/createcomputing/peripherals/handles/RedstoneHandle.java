package de.saschat.createcomputing.peripherals.handles;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import de.saschat.createcomputing.peripherals.ComputerizedRedstoneLinkPeripheral;
import de.saschat.createcomputing.tiles.ComputerizedRedstoneLinkTile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static de.saschat.createcomputing.Utils.getByName;

public class RedstoneHandle {
    public ComputerizedRedstoneLinkPeripheral parent;
    public UUID handle;
    public boolean open = true;
    public RedstoneHandle(ComputerizedRedstoneLinkPeripheral parent, UUID pair) {
        this.handle = pair;
        this.parent = parent;
    }

    public void isOpen() throws LuaException {
        if(!open)
            throw new LuaException("This handle has already been closed.");
    }


    public ComputerizedRedstoneLinkTile.LinkPair getHandle() throws LuaException {
        isOpen();
        ComputerizedRedstoneLinkTile.LinkPair linkPair = parent.parent.pairs.get(handle);
        if(linkPair == null)
            throw new LuaException("Pair has already been deleted.");
        return linkPair;
    }

    @LuaFunction(mainThread = false)
    public final void setSignal(IArguments arguments) throws LuaException {
        isOpen();
        int value = arguments.getInt(0);
        getHandle().provideSignal(value);
    }
    @LuaFunction(mainThread = false)
    public final int getSignal(IArguments arguments) throws LuaException {
        isOpen();
        return getHandle().retrieveSignal();
    }
    @LuaFunction
    public final String getId() throws LuaException {
        return this.handle.toString();
    }
    @LuaFunction(mainThread = true)
    public final void setItems(IArguments arguments) throws LuaException {
        isOpen();
        String _item1 = arguments.getString(0);
        String _item2 = arguments.getString(1);
        Item item1 = getByName(new ResourceLocation(_item1));
        Item item2 = getByName(new ResourceLocation(_item2));
        if (item1 == null)
            throw new LuaException(_item1 + " is not a valid item id!");
        if (item2 == null)
            throw new LuaException(_item2 + " is not a valid item id!");
        if (!ComputerizedRedstoneLinkTile.checkItem(item1))
            throw new LuaException(_item1 + " is banned from the Computerized Redstone Link! This can be changed in the config.");
        if (!ComputerizedRedstoneLinkTile.checkItem(item2))
            throw new LuaException(_item2 + " is banned from the Computerized Redstone Link! This can be changed in the config.");
        getHandle().setFrequency(new Item[] {item1, item2});
    }
    @LuaFunction(mainThread = true)
    public final String[] getItems(IArguments arguments) throws LuaException {
        return Arrays.stream(getHandle().items).map(a -> ForgeRegistries.ITEMS.getKey(a).toString()).collect(Collectors.toList()).toArray(new String[0]);
    }
    @LuaFunction
    public final void close() {
        this.open = false;
        parent.removeHandle(handle);
    }
}
