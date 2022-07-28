package de.saschat.createcomputing.api;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public abstract class SmartPeripheral implements IDynamicPeripheral {
    List<String> names = new LinkedList<>();
    List<PeripheralMethod> methods = new LinkedList<>();

    public void addMethod(String name, PeripheralMethod method) {
        names.add(name);
        methods.add(method);
    }

    public PeripheralMethod removeMethod(String name) {
        int i = names.indexOf(name);
        names.remove(i);
        return methods.remove(i);
    }

    @NotNull
    @Override
    public String[] getMethodNames() {
        return names.toArray(new String[0]);
    }

    @NotNull
    @Override
    public MethodResult callMethod(@NotNull IComputerAccess iComputerAccess, @NotNull ILuaContext iLuaContext, int i, @NotNull IArguments iArguments) throws LuaException {
        return methods.get(i).run(iComputerAccess, iLuaContext, iArguments);
    }
}
