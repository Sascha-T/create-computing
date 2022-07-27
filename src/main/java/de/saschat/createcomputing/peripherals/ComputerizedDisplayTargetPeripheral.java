package de.saschat.createcomputing.peripherals;

import com.google.gson.Gson;
import com.simibubi.create.content.logistics.block.display.DisplayLinkContext;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import de.saschat.createcomputing.CreateComputingMod;
import de.saschat.createcomputing.tiles.ComputerizedDisplayTargetTile;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ComputerizedDisplayTargetPeripheral implements IDynamicPeripheral {
    ComputerizedDisplayTargetTile parent;

    public ComputerizedDisplayTargetPeripheral(ComputerizedDisplayTargetTile computerizedDisplayTargetTile) {
        this.parent = computerizedDisplayTargetTile;
    }

    public List<IComputerAccess> computers = new LinkedList<>();

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
        return new String[]{"setWidth", "getWidth", "setHeight", "getHeight"};
    }

    @NotNull
    @Override
    public MethodResult callMethod(@NotNull IComputerAccess iComputerAccess, @NotNull ILuaContext iLuaContext, int i, @NotNull IArguments iArguments) throws LuaException {
        switch (i) {
            case 0: {
                int width = iArguments.getInt(0);
                parent.maxWidth = width;
            }
            case 1: {
                return MethodResult.of(parent.maxWidth);
            }
            case 2: {
                int height = iArguments.getInt(0);
                parent.maxHeight = height;
            }
            case 3: {
                return MethodResult.of(parent.maxHeight);
            }
        }
        return MethodResult.of();
    }


    @NotNull
    @Override
    public String getType() {
        return new ResourceLocation(CreateComputingMod.MOD_ID, "computerized_display_target").toString();
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return false;
    }

    public Gson gson = new Gson();

    public void acceptText(int line, List<MutableComponent> list, DisplayLinkContext displayLinkContext) {
        Map<Double, Object> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            map.put((double) i, gson.fromJson(Component.Serializer.toJson(list.get(i)), Map.class)); // this code is terrific
        }
        computers.forEach(a -> {
            a.queueEvent("display_link_data",
                displayLinkContext.te().activeSource.id.toString(),
                displayLinkContext.getSourcePos().getX(),
                displayLinkContext.getSourcePos().getY(),
                displayLinkContext.getSourcePos().getZ(),
                line,
                map
            );
        });
    }
}
