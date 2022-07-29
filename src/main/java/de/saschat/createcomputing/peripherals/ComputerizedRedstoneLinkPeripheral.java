package de.saschat.createcomputing.peripherals;

import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.saschat.createcomputing.CreateComputingMod;
import de.saschat.createcomputing.Utils;
import de.saschat.createcomputing.api.SmartPeripheral;
import de.saschat.createcomputing.config.CreateComputingConfigServer;
import de.saschat.createcomputing.peripherals.handles.RedstoneHandle;
import de.saschat.createcomputing.tiles.ComputerizedRedstoneLinkTile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ComputerizedRedstoneLinkPeripheral extends SmartPeripheral {
    public ComputerizedRedstoneLinkTile parent;
    public HashMap<UUID, RedstoneHandle> handles = new HashMap<>();

    public void removeHandle(UUID hd) {
        handles.values().forEach(a -> {
            if (a.handle.equals(hd)) {
                a.close();
                handles.remove(hd, a);
            }
        });
    }

    public void killHandles(UUID hd) {
        handles.values().forEach(a -> {
            if (a.handle.equals(hd)) {
                a.close();
                handles.remove(a.handle, a);
            }
        });
    }

    public ComputerizedRedstoneLinkPeripheral(ComputerizedRedstoneLinkTile tile) {
        this.parent = tile;

        addMethod("clearHandles", (iComputerAccess, iLuaContext, iArguments) -> {
            parent.tasks.add(() -> {
                handles.clear();
                parent.pairs.forEach((b,a) -> {
                    parent.remove(a);
                });
            });
            return MethodResult.of();
        });
        addMethod("getMaxHandles", (iComputerAccess, iLuaContext, iArguments) -> {
            return MethodResult.of(CreateComputingConfigServer.get().MAXIMUM_CONCURRENT_LINKS.get());
        });
        addMethod("closeHandle", (iComputerAccess, iLuaContext, iArguments) -> {
            return MethodResult.of();
        });
        addMethod("getHandles", (iComputerAccess, iLuaContext, iArguments) -> {
            Map<String, RedstoneHandle> hdl = new HashMap<>();
            for (Map.Entry<UUID, RedstoneHandle> uuidRedstoneHandleEntry : handles.entrySet()) {
                hdl.put(uuidRedstoneHandleEntry.getKey().toString(), uuidRedstoneHandleEntry.getValue());
            }
            return MethodResult.of(hdl);
        });
        addMethod("openHandle", (iComputerAccess, iLuaContext, iArguments) -> {
            String _item1 = iArguments.getString(0);
            String _item2 = iArguments.getString(1);
            Item item1 = Utils.getByName(new ResourceLocation(_item1));
            Item item2 = Utils.getByName(new ResourceLocation(_item2));
            if (item1 == null)
                throw new LuaException(_item1 + " is not a valid item id!");
            if (item2 == null)
                throw new LuaException(_item2 + " is not a valid item id!");
            if (!ComputerizedRedstoneLinkTile.checkItem(item1))
                throw new LuaException(_item1 + " is banned from the Computerized Redstone Link! This can be changed in the config.");
            if (!ComputerizedRedstoneLinkTile.checkItem(item2))
                throw new LuaException(_item2 + " is banned from the Computerized Redstone Link! This can be changed in the config.");
            ComputerizedRedstoneLinkTile.LinkPair pair = new ComputerizedRedstoneLinkTile.LinkPair(parent, new Item[]{item1, item2});
            pair.setFrequency(pair.items);
            UUID data = parent.add(pair);
            if(data == null)
                throw new LuaException("You have exceeded the maximum amount of frequencies for this peripheral. The maximum can be changed in the config.");
            RedstoneHandle redstoneHandle = new RedstoneHandle(this, data);
            handles.put(redstoneHandle.handle, redstoneHandle);
            return MethodResult.of(redstoneHandle);
        });
    }

    @NotNull
    @Override
    public String getType() {
        return new ResourceLocation(CreateComputingMod.MOD_ID, "computerized_redstone_link").toString();
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }
}
