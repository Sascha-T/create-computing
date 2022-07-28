package de.saschat.createcomputing.api;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import org.jetbrains.annotations.NotNull;

public interface PeripheralMethod {
    MethodResult run(@NotNull IComputerAccess iComputerAccess, @NotNull ILuaContext iLuaContext, @NotNull IArguments iArguments) throws LuaException;
}
